(ns stack-spike.core
  (:require [goog.dom :as dom]))


(let [fragment (dom/htmlToDocumentFragment "<h1>Hi!</h1>")
      body (aget (dom/getElementsByTagNameAndClass "body") 0)]
  (dom/insertChildAt body fragment 0))
