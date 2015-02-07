(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om.dom]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [cljs-http.client :as http]
            [clojure.browser.repl]
            [figwheel.client :as figwheel :include-macros true]
            [stack-spike.tools :refer [log]]
            [stack-spike.shared.routes :refer [routes resolve-url]]
            [cljs.reader :as edn]
            [goog.dom])
  (:import [goog Uri]))

(defn current-url []
  (if (exists? js/document)
    (.-href (.-location js/document))
    "/ships#nashorn"))

(if (exists? js/console)
  (enable-console-print!)
  (set-print-fn! js/print))

(def app-state
  (atom {:page nil
         :ships nil}))

(def action-chan (chan))

(defn set-page [path]
  (om/update! (om/root-cursor app-state) :page (resolve-url path)))

(defn goto [url]
  (.pushState js/history {} nil url )
  (set-page url))

(defn navigate [event]
  (.preventDefault event)
  (goto (-> event .-target .-href))
  nil)

(defn activate [event action arg]
  (.preventDefault event)
  (put! action-chan [action arg])
  nil)

(defn ship-row [id-ship-pair owner]
  (reify
    om/IRender
    (render [this]
      (let [ship (last id-ship-pair)]
        (dom/tr {:id (str "ship-" (:db/id ship)) :class "ship"}
                (dom/td {:class "id"}
                        (dom/a {:href (str "/ships/" (:db/id ship))
                                :class "edit"
                                :on-click navigate}
                               (:db/id ship)))
                (dom/td {:class "name"}
                        (:ship/name ship))
                (dom/td {:class "controls"}
                        (dom/a {:class "delete" :href "#"
                                :on-click #(activate % :request-ship-delete @ship)}
                               "[delete]")))))))

(defn ship [ship owner]
  (reify
    om/IInitState
    (init-state [this]
      ship)

    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit #(activate % :request-ship-update (om/get-state owner))}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :auto-focus true
                            :on-change #(om/set-state! owner :ship/name (-> % .-target .-value))})
                (dom/input {:type "submit" :value "Update Ship"})))))


(defn new-ship [ship owner]
  (reify
    om/IInitState
    (init-state [this]
      ship)

    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit #(activate % :request-ship-create (om/get-state owner))}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :auto-focus true
                            :on-change #(om/set-state! owner :ship/name (-> % .-target .-value))})
                (dom/input {:type "submit" :value "Create Ship"})))))

(defn ships [ships owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (dom/table
                    {:class "ships"}
                    (dom/thead
                     (dom/th "id")
                     (dom/th "name"))
                    (dom/tbody
                     (om/build-all ship-row ships)))
               (dom/a {:class "new-ship" :href "/ships/new" :on-click navigate } "New Ship")))
    ))

(defn loading []
  (om/component
   (dom/h1 {:class "loading"} "Loading...")))

(defn not-found []
  (om/component
   (dom/div
    (dom/h1 nil "Not found.")
    (dom/p (pr-str @app-state)))))

(defn render-page [app]
  (if (nil? (:ships app))
    (om/build loading nil)
    (condp = (get-in app [:page :handler])
      :ships (om/build ships (get app :ships))
      :ship (let [ship-id (get-in app [:page :route-params :id])]
              (prn ship-id)
              (if (= "new" ship-id)
                (om/build new-ship {:db/id nil :ship/name ""})
                (om/build ship
                          (get (get app :ships)
                               (long ship-id)))))
      (om/build not-found nil))))

(defn fetch-ships [app]

  (go (let [response (<! (http/get "/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (om/update! app :ships (:body response)))))

(defn page [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (render-page app)))
    om/IWillMount
    (will-mount [this]
      #_(fetch-ships app))))

(def csrf-token
  (if (exists? js/document)
    (.-content (.getElementById js/document "csrf-token"))
    "(figure out how to handle csrf in nashorn)"))

(defn de-namespace-keys [m]
  (apply hash-map (mapcat (fn [[k v]] [(-> k name keyword) v]) m)))



(declare app-container
         app-state)

(defn request-action [message]
  (http/post "/api/action"
             {:transit-params message
              :headers {"Accept" "application/transit+json;verbose"
                        "X-CSRF-Token" csrf-token}})  )

(defn main []
  (set! (.-onpopstate js/window) (fn [e]
                                   (set-page (current-url))))
  (go-loop []
    (let [action (<! action-chan)]
      (log action)
      (request-action action)
      (prn @app-state)
      (recur)))
  (om/root page app-state
             {:target (. js/document (getElementById "root"))
              :tx-listen prn
              :shared {:action-chan action-chan}}))


(defn ^:export render-to-string
  "Takes an app state as EDN and returns the HTML for that state.
  It can be invoked from JS as `omelette.view.render_to_string(edn)`."
  [state-edn]
  (->> state-edn
       edn/read-string
       (om/build page)
       om.dom/render-to-str))

(defn ^:export init
  "Initializes the app.
  Should only be called once on page load.
  It can be invoked from JS as `omelette.view.init(appElementId, stateElementId)`."
  [app-id state-id]
  (->> state-id
       goog.dom/getElement
       .-textContent
       edn/read-string
       atom
       (set! app-state))
  (->> app-id
       goog.dom/getElement
       (set! app-container))
  (main))
