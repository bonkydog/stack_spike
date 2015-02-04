(ns stack-spike.interface.html.home
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup.element :refer [link-to javascript-tag]]))


(defn home []
  (html
   [:html
    [:head]
    [:body
     [:h1 "Hello!"]
     [:p "HTML interface is no longer supported."]
     [:p "Om interface is here:"
      [:a {:href "/ships"} "OM"]]]]))

(defn om []
  (html
   [:html
    [:head
     [:meta {:id "csrf-token" :name "csrf-token" :content *anti-forgery-token*}]]
    [:body
     [:div#root]
     [:script {:type "text/javascript" :src "/js/react.js"}]
     [:script {:type "text/javascript" :src "/js/out/goog/base.js"}]
     [:script {:type "text/javascript" :src "/js/app.js"}]
     [:script "goog.require('stack_spike.om_app');"]
     [:script "goog.require('stack_spike.dev');"]]]))
