(ns stack-spike.interface.html.home
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup.element :refer [link-to javascript-tag]]))

(defn home [_]
  (html
   [:html
    [:head
     [:meta {:name "csrf-token" :content *anti-forgery-token*}]]
    [:body
     [:div#root]
     [:script {:type "text/javascript" :src "js/react.js"}]
     [:script {:type "text/javascript" :src "js/out/goog/base.js"}]
     [:script {:type "text/javascript" :src "js/main.js"}]
     (javascript-tag "goog.require('stack_spike.om_app');")]]))
