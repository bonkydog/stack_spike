(ns stack-spike.om-app
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.browser.repl]
            [stack-spike.tools :refer [log]])))

(ws-repl/connect "ws://localhost:9001")

(def app-state
  (atom {:page "index"
         :ships [{:id 23 :name "foo"}
                 {:id 17 :name "bar"}]}))

(defn ship-row [ship]
  (om/component
   (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
           (dom/td {:id "id"}
                   (dom/a {:href (str "/ship/" (:id ship)) :className "edit"}
                          (:id ship)))
           (dom/td {:className "id"}
                   (:name ship)))))

(defn ships-index [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/table {:className "ships"}
                 (apply dom/tbody nil
                        (om/build-all ship-row (:ships app)))))))

(om/root ships-index
         app-state
         {:target (. js/document (getElementById "root"))})
