(ns stack-spike.interface.html.ship
  (:require [stack-spike.interface.routes :refer [routes]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [stack-spike.utility.debug :refer [dbg]]))


(defn show [ship]
  (html [:html
         [:body
          [:p (str "Hello, this is the ship page for " (:ship/name ship))]
          (form-to [:post (:collection-path ship)]
                   (anti-forgery-field)
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
                [:td (link-to {:class "ship"} (:path ship) (:db/id ship))]
                [:td (:ship/name ship)]])
             ships)]]]]]))
