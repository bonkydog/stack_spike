(ns stack-spike.interface.html.ship
  (:require [stack-spike.interface.routes :refer [routes]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [stack-spike.utility.debug :refer [dbg]]))


(defn show [ship]
  (dbg ship)
  (html [:html
         [:body
          [:p (str "Hello, this is the ship page for " (:ship/name ship))]
          (form-to [:put (:path ship)]
                   (label "name" "Name")
                   (text-field "name" (:ship/name ship))
                   (submit-button "Update Ship"))]]))

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
                [:td (link-to (:path ship) (:db/id ship))]
                [:td (:ship/name ship)]])
             ships)]]]]]))
