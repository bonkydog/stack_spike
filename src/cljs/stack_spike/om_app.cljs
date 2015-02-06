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
            [stack-spike.routes :refer [routes current-url resolve]]
            [cljs.reader :as edn]
            [goog.dom])
  (:import [goog Uri]))

(if (exists? js/console)
  (enable-console-print!)
  (set-print-fn! js/print))

(def app-state
  (atom {:page (resolve (current-url))
         :ships nil}))


(defn navigate [owner event]
  (.preventDefault event)
  (put! (om/get-shared owner :nav-chan) (.-href (.-target event)))
  nil)


(defn ship-row [id-ship-pair owner]
  (reify
    om/IInitState
    (init-state [_] (last id-ship-pair))
    om/IRenderState
    (render-state [this state] 
      (let [ship (last id-ship-pair)]
        (dom/tr {:id (str "ship-" (:db/id ship)) :class "ship"}
                (dom/td {:class "id"}
                        (dom/a {:href (str "/ships/" (:db/id ship))
                                :class "edit"
                                :on-click (partial navigate owner)}
                               (:db/id ship)))
                (dom/td {:class "name"}
                        (:ship/name ship))
                (dom/td {:class "controls"}
                        (dom/a {:class "delete"
                                :href "#"
                                :on-click (fn [e]
                                            (.preventDefault e)
                                            (put! (om/get-shared owner :delete-chan) state)
                                            nil)}
                               "[delete]")))))))

(defn handle-change [e owner {:keys [name]}]
  (.preventDefault e)
  (println "handling change!")
  (om/set-state! owner :ship/name (.. e -target -value)))




(defn ship [ship owner]
  (reify
    om/IInitState
    (init-state [_]
      {:db/id (:db/id ship) :ship/name (:ship/name ship)})
    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit (fn [e]
                                                           (.preventDefault e)
                                                           (put! (om/get-shared owner :update-chan) state )
                                                           nil
                                                           )}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :autofocus "autofocus"
                            :on-change #(handle-change % owner state)})
                (dom/input {:type "submit" :value "Update Ship"})))))


(defn new-ship [ship owner]
  (reify
    om/IInitState
    (init-state [_]
      {:db/id (:db/id ship) :ship/name (:ship/name ship)})
    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit (fn [e]
                                                           (.preventDefault e)
                                                           (put! (om/get-shared owner :create-chan) state )
                                                           nil)}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :autofocus "autofocus"
                            :on-change #(handle-change % owner state)})
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
               (dom/a {:class "new-ship" :href "/ships/new" :on-click (partial navigate owner) } "New Ship")))
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
  (println "FETCHING SHIPS NOW!!!")
  (go (let [response (<! (http/get "/api/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (println "GOT A RESPONSE!!!")
        (prn response)
        (om/update! app :ships (:body response)))))


(defn page [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (render-page app)))
    om/IWillMount
    (will-mount [this]
      #_(fetch-ships app))))

(defn set-page [path]
  (om/update! (om/root-cursor app-state) :page (resolve path)))

(defn goto [url]
  (.pushState js/history {} nil url )
  (set-page url))

(def csrf-token
  (if (exists? js/document)
    (.-content (.getElementById js/document "csrf-token"))
    "(figure out how to handle csrf in nashorn)"))

(defn de-namespace-keys [m]
  (apply hash-map (mapcat (fn [[k v]] [(-> k name keyword) v]) m)))


(declare app-container
         app-state)

(defn main []
  (let [nav-chan (chan)
        update-chan (chan)
        create-chan (chan)
        delete-chan (chan)]
    (go-loop []
      (alt! nav-chan ([url]
                      (log "nav-chan: " url)
                      (goto url)
                      (log "nav-chan complete!"))
            update-chan ([ship-update]
                         (http/post "/api/action"
                                    {:transit-params [:update-ship (de-namespace-keys ship-update)]
                                     :headers {"Accept" "application/transit+json;verbose"
                                               "X-CSRF-Token" csrf-token}})
                         (om/update! (om/root-cursor app-state) [:ships (:db/id ship-update)] ship-update)
                         (goto "/ships"))
            create-chan ([ship-creation]
                         (let [response (<! (http/post "/api/action"
                                                       {:transit-params [:create-ship (de-namespace-keys  ship-creation)]
                                                        :headers {"Accept" "application/transit+json;verbose"
                                                                  "X-CSRF-Token" csrf-token}}))]
                           (om/update! (om/root-cursor app-state) [:ships (:db/id (get-in response [:body :result]))] (get-in response [:body :result]))
                           (goto "/ships")))
            delete-chan ([ship-deletion]
                         (http/post "/api/action"
                                    {:transit-params [:delete-ship (de-namespace-keys ship-deletion)]
                                     :headers {"Accept" "application/transit+json;verbose"
                                               "X-CSRF-Token" csrf-token}})
                         (om/transact! (om/root-cursor app-state) :ships #(dissoc % (:db/id ship-deletion)))))

      (recur))

    (set! (.-onpopstate js/window) (fn [e]
                                     (log e)
                                     (log "pop!")
                                     (set-page (current-url))))
    (om/root page
             app-state
             {:target app-container
              :shared {:nav-chan nav-chan
                       :update-chan update-chan
                       :create-chan create-chan
                       :delete-chan delete-chan}})))


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
