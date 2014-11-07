(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.presenter.ship :as ship-presenter]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship]]
            [stack-spike.external.database :refer [entity-gateway]]))

(defresource ship [db]
  :available-media-types ["text/html"]
  :exist? (fn [ctx] )
  :handle-ok (fn [ctx] (ship-presenter/present-ship-show (view-ship (entity-gateway db) (Long/parseLong (get-in ctx [:request :params :id]))))))

(defresource ship-list [db]
  :available-media-types ["text/html"]
  :handle-ok (fn [req] (ship-presenter/present-ship-index (list-ships (entity-gateway db)))))
