(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [cljs-http.client :as http]
            [clojure.browser.repl]
            [stack-spike.tools :refer [log]]))

(enable-console-print!)

(def app-state
  (atom {:page "index"
         :ships []}))

(defn ship-row [ship]
  (om/component
   (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
           (dom/td #js{:id "id"}
                   (dom/a #js{:href (str "#/ships/" (:db/id ship)) :className "edit"}
                          (:db/id ship)))
           (dom/td #js{:className "id"}
                   (:ship/name ship)))))

(defn ships-index [app owner]
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

(defn fetch-ships [app]
  (go (let [response (<! (http/get "/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (om/update! app :ships (vec (:body response))))))

(om/root ships-index
         app-state
         {:target (. js/document (getElementById "root"))})
