(ns stack-spike.external.html.ship
  (:require
   [hiccup.core :refer [html]]))


(defn show [id]
  (html [:html
         [:body
          [:p (str "Hello, this is the ship page for " id)]]]))

(defn index []
  (html
   [:html
    [:body
     [:table {:class "ships"}
      [:thead
       [:tr
        [:th "name"]]
       [:tbody
        [:tr
         [:td "foo"]]]]]]]))
