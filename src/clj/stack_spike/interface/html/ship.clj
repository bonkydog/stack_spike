(ns stack-spike.interface.html.ship
  (:require [hiccup.core :refer [html]]
            [stack-spike.utility.debug :refer [dbg]]))


(defn show [ship]
  (html [:html
         [:body
          [:p (str "Hello, this is the ship page for " (:ship/name (dbg ship)))]]]))

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
                [:td (:db/id ship)]
                [:td (:ship/name ship)]])
             ships)]]]]]))
