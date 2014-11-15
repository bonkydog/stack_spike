(ns stack-spike.interface.html.ship
  (:require [stack-spike.interface.routes :refer [routes]]
            [bidi.bidi :refer [path-for]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]))


(defn show [ship]
  (html [:html
         [:body
          [:p (str "Hello, this is the ship page for " (:ship/name ship))]]]))

(defn index [ships]
  (html
   [:html
    [:body
     [:table {:class "ships"}
      [:thead
       [:tr
        [:th "id"]
        [:th "name"]]
       [:tbody
        (map (fn [ship] 
               [:tr
                [:td (link-to (path-for routes :ship :id (:db/id ship)) (:db/id ship))]
                [:td (:ship/name ship)]])
             ships)]]]]]))
