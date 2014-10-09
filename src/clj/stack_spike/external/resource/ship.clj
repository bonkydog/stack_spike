(ns stack-spike.external.resource.ship
  (:require [liberator.core :refer [defresource]]
            [hiccup.core :refer [html]]))

(defresource ship
  :available-media-types ["text/html"]
  :handle-ok (fn [req] (html [:html
                           [:body
                            [:p (str "Hello, this is the ship page for "
                                     (get-in req [:request :params :id]))]]])))

(defresource ship-list
  :available-media-types ["text/html"]
  :handle-ok (fn [_] (html [:html
                           [:body
                            [:table {:class "ships"}
                             [:thead
                              [:tr
                               [:th "name"]]
                              [:tbody
                               [:tr
                                [:td "foo"]]]]]]])))
