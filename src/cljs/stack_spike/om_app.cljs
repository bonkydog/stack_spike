(ns stack-spike.om-app
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


(def app-state
  (atom {:page "index"
         :ships []}))

;; (defn ship-row [ship]
;;   (om/component
;;    (dom/tr {:id (str "ship-" (:id ship)) :className "ship"}
;;            (dom/td {:id "id"}
;;                    (dom/a {:href (str "/ship/" (:id ship)) :className "edit"}
;;                           (:id ship)))
;;            (dom/td {:className "id"}
;;                    (:name ship)))))

;; (defn ship-index-table [ships]
;;   (om/component
;;    (dom/table {:className "ships"}
;;               (dom/tbody nil ))))

(defn widget [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil (:text data)))))

(om/root widget {:text "Hello world!"}
         {:target (. js/document (getElementById "root"))})
