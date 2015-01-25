(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [cljs-http.client :as http]
            [bidi.bidi :as bidi]
            [clojure.browser.repl]
            [weasel.repl :as weasel]
            [figwheel.client :as figwheel :include-macros true]
            [stack-spike.tools :refer [log]]))

(enable-console-print!)


(def routes
  ["/om" {"/ships" :ships
          ["/ships/" :id] :ship}])


(defn current-url []
  (.-href (.-location js/document)))

(defn resolve [url]
  (let [path (.-pathname (js/URL.  url))]
    (log "path:" path)
    (bidi/match-route routes path)))

(def app-state
  (atom {:page (resolve (current-url))
         :ships nil}))

(defn ship-row [id-ship-pair owner]
  (om/component
   (let [ship (last id-ship-pair)]
     (dom/tr {:id (str "ship-" (:id ship)) :class "ship"}
             (dom/td {:id "id"}
                     (dom/a {:href (str "/om/ships/" (:db/id ship))
                                :class "edit"
                                :on-click (fn [event]
                                           (.preventDefault event)
                                           (put! (om/get-shared owner :nav-chan) (.-href (.-target event))))}
                            (:db/id ship)))
             (dom/td {:class "id"}
                     (:ship/name ship))))))

(defn handle-change [e owner {:keys [name]}]
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
                                                           (put! (om/get-shared owner :update-chan) state ))}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state)
                            :on-change #(handle-change % owner state)})
                (dom/input {:type "submit" :value "Update Ship"})))))

(defn ships [ships owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (dom/table {:class "ships"}
                          (apply dom/tbody nil
                                 (om/build-all ship-row ships)))
               (dom/a {:class "new-ship" :href "/om/ships/new"} "New Ship")))
    ))

(defn loading []
  (om/component
   (dom/h1 "Loading...")))

(defn not-found []
  (om/component
   (dom/h1 nil "Not found.")))

(defn render-page [app]
  (if (nil? (:ships app))
    (om/build loading nil)
    (condp = (get-in app [:page :handler])
      :ships (om/build ships (get app :ships))
      :ship (om/build ship
                      (get (get app :ships)
                           (long (get-in app [:page :route-params :id]))))
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
      (fetch-ships app))))

(defn set-page [url]
  (om/update! (om/root-cursor app-state) :page (resolve url)))

(defn goto [url]
  (.pushState js/history {} nil url )
  (set-page url))

(defn main []
  (let [nav-chan (chan)
        update-chan (chan)]
    (go-loop []
      (alt! nav-chan ([url]
                      (log "nav-chan: " url)
                      (goto url)
                      (log "nav-chan complete!"))
            update-chan ([ship-update]
                         (om/update! (om/root-cursor app-state) [:ships (:db/id ship-update)] ship-update)
                         (.back js/history)))

      (recur))

    (set! (.-onpopstate js/window) (fn [e]
                                     (log e)
                                     (log "pop!")
                                     (set-page (current-url))))
    (om/root page
             app-state
             {:target (. js/document (getElementById "root"))
              :shared {:nav-chan nav-chan
                       :update-chan update-chan}})))
