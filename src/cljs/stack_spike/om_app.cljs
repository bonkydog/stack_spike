(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [cljs-http.client :as http]
            [bidi.bidi :as bidi]
            [clojure.browser.repl]
            [stack-spike.tools :refer [log]]))

(enable-console-print!)

(def routes
  ["" {"" :home
       "#/ships" :ships
       ["#/ships/" :id] :ship}])


(defn fragment []
  (.-hash (.-location js/document)))

(defn resolve [fragment]
  (:handler (bidi/match-route routes fragment)))

(def app-state
  (atom {:page (resolve (fragment))
         :ships []}))

(prn @app-state)


(defn ship-row [ship owner]
  (om/component
   (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
           (dom/td #js{:id "id"}
                   (dom/a #js{:href (str "#/ships/" (:db/id ship))
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
      (dom/h1 nil "OHAI! I AM A SHIP!"))))

(defn ships [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/text nil
       (dom/table #js{:className "ships"}
                  (apply dom/tbody nil
                         (om/build-all ship-row (:ships app))))
       (dom/a #js{:className "new-ship" :href "#/ships/new"} "New Ship")))
    om/IWillMount
    (will-mount [this]
      (fetch-ships app))))


(defn page [app owner]
  (reify
    om/IRender
    (render [this]
      (let [page-component (page-components (:page app))]
        (if page-component
          (om/build page-component app)
          (dom/div nil "Error!" (:page app)))))))

(def page-components
  {:home ships
   :ships ships
   :ship ship})


(defn fetch-ships [app]
  (go (let [response (<! (http/get "/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (om/update! app :ships (vec (:body response))))))

(defn set-page [url]
  (let [fragment (.-hash (js/URL. url))]
    (om/update! (om/root-cursor app-state) :page (resolve fragment))))

(defn goto [url]
  (.pushState js/history {} nil url )
  (set-page url))

(let [nav-chan (chan)]
  (go-loop [url (<! nav-chan)]
    (goto url))
  (set! (.-onpopstate js/window) (fn []
                                   (log "pop!")
                                   (set-page (.-href (.-location js/window)))))
  (om/root page
           app-state
           {:target (. js/document (getElementById "root"))
            :shared {:nav-chan nav-chan}}))
