(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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
         :ships []}))

(defn ship-row [id-ship-pair owner]
  (om/component
   (let [ship (last id-ship-pair)]
     (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
             (dom/td #js{:id "id"}
                     (dom/a #js{:href (str "/om/ships/" (:db/id ship))
                                :className "edit"
                                :onClick (fn [event]
                                           (.preventDefault event)
                                           (put! (om/get-shared owner :nav-chan) (.-href (.-target event))))}
                            (:db/id ship)))
             (dom/td #js{:className "id"}
                     (:ship/name ship))))))

(defn ship [ship owner]
  (log "got to ship!")
  (log ship)
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "OHAI! I AM SHIP #" (:db/id ship)  "!"))))

(defn ships [ships owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (dom/table #js{:className "ships"}
                          (apply dom/tbody nil
                                 (om/build-all ship-row ships)))
               (dom/a #js{:className "new-ship" :href "/om/ships/new"} "New Ship")))
    ))

(defn not-found []
  (om/component
   (dom/h1 nil "Not found.")))

(defn render-page [app]
  (condp = (get-in app [:page :handler])
    :ships (om/build ships (get app :ships))
    :ship (om/build ship
                    (get (get app :ships)
                         (long (get-in app [:page :route-params :id]))))
    (om/build not-found nil)))

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
  (let [nav-chan (chan)]
    (go-loop []
      (let [url (<! nav-chan)]
        (log "nav-chan: " url)
        (goto url)
        (log "nav-chan complete!"))
      (recur))

    (set! (.-onpopstate js/window) (fn [e]
                                     (log e)
                                     (log "pop!")
                                     (set-page (current-url))))
    (om/root page
             app-state
             {:target (. js/document (getElementById "root"))
              :shared {:nav-chan nav-chan}})))
