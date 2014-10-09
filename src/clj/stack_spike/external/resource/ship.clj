(ns stack-spike.external.resource.ship
  (:require [liberator.core :refer [defresource]]
            [stack-spike.external.html.ship :as html]))

(defresource ship [db]
  :available-media-types ["text/html"]
  :handle-ok (fn [req] (html/show (get-in req [:request :params :id]))))

(defresource ship-list [db]
  :available-media-types ["text/html"]
  :handle-ok (fn [req] (html/index)))
