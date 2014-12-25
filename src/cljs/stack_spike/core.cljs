(ns stack-spike.core
  (:require [goog.dom :as dom]
            [goog.events :as e]))


(defn log [x]
  (.log js/console x)
  x)

(defn csrf-token[]
  (if-let [meta-tag (.querySelector js/document "html > head > meta[name='csrf-token']")]
    (.-value (aget (.-attributes meta-tag) "content"))))

(defn handle-delete [e]
  (if (and (= "A" (.-tagName (.-target e)))
           (some #(= "delete") (prim-seq (.-classList (.-target e)))))
    (let [action (.-value (aget (.-attributes (.-target e)) "data-action"))
          method (.-value (aget (.-attributes (.-target e)) "data-method"))
          body (aget (.getElementsByTagName js/document "body") 0)
          form (dom/createDom "form" (js-obj "action" action "method" "POST")
                         (dom/createDom "input" (js-obj "type" "hidden"
                                                        "name" "__anti-forgery-token"
                                                        "value" (csrf-token)))
                         (dom/createDom "input" (js-obj "type" "hidden"
                                                        "name" "_method"
                                                        "value" method)))]
      (dom/appendChild body form)
      (.submit form))))

(let [fragment (dom/htmlToDocumentFragment "<h1>Hi!</h1>")
      body (aget (.getElementsByTagName js/document "body") 0)]
  (e/listen body (. e/EventType -CLICK) handle-delete))
