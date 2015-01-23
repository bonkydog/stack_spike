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
  (.-href (.-location js/window)))

(defn resolve [url]
  (let [path (.-pathname (js/URL.  url))]
    (log "path:" path)
    (bidi/match-route routes path)))

(def app-state
  (atom {:page (resolve (current-url))
         :ships []}))

(prn @app-state)


(defn ship-row [ship owner]
  (om/component
   (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
           (dom/td #js{:id "id"}
                   (dom/a #js{:href (str "/om/ships/" (:db/id ship))
                              :className "edit"
                              :onClick (fn [event]
                                         (.preventDefault event)
                                         (put! (om/get-shared owner :nav-chan) (.-href (.-target event))))}
                          (:db/id ship)))
           (dom/td #js{:className "id"}
                   (:ship/name ship)))))

(defn ship [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "OHAI! I AM SHIP #" (get-in app [:page :route-params :id])  "!"))))

(defn ships [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/text nil
       (dom/table #js{:className "ships"}
                  (apply dom/tbody nil
                         (om/build-all ship-row (:ships app))))
       (dom/a #js{:className "new-ship" :href "/om/ships/new"} "New Ship")))
    ))


(defn page [app owner]
  (reify
    om/IRender
    (render [this]
      (let [page-component (page-components (get-in app [:page :handler]))]
        (if page-component
          (om/build page-component app)
          (dom/div nil "Error!" (str (:page app))))))
    om/IWillMount
    (will-mount [this]
      (fetch-ships app))))

(def page-components
  {:ships ships
   :ship ship})

(defn fetch-ships [app]
  (go (let [response (<! (http/get "/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (om/update! app :ships (vec (:body response))))))

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

    (log "GOT HERE")
    (set! (.-onpopstate js/window) (fn []
                                     (log "pop!")
                                     (set-page (current-url))))
    (om/root page
             app-state
             {:target (. js/document (getElementById "root"))
              :shared {:nav-chan nav-chan}})))
