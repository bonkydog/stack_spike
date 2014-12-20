(ns stack-spike.interface.html.ship
  (:require [stack-spike.interface.routes :as r]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [stack-spike.utility.debug :refer [dbg]]))


(defn show [ship]
  (let [form-target (if (:db/id ship)
                      [:put (:path ship)]
                      [:post (:collection-path ship)])]
    (html [:html
           [:body
            (form-to {:class "ship"} form-target
                     (anti-forgery-field)
                     (label "name" "Name")
                     (text-field "name" (:ship/name ship))
                     (submit-button "Update Ship"))]])))

(defn index [ships]
  (html
   [:html
    [:body
     [:table.ships
      [:thead
       [:tr
        [:th "id"]
        [:th "name"]
        [:th]]
       [:tbody
        (map (fn [ship]
               [:tr.ship {:id (str "ship-" (:db/id ship))}
                [:td.id (link-to {:class "edit"} (:path ship) (:db/id ship))]
                [:td.name (:ship/name ship)]
                [:td.controls
                 [:a.delete {:href "#"} "[delete]"]]])
             ships)]]]
     [:a.new-ship {:href (r/path-for :ship :id "new")} "New Ship"]]]))
