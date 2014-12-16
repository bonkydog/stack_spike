(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.presenter.ship :as ship-presenter]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug]]))

(defresource ship [db]
  :available-media-types ["text/html"]

  :exists? (fn [ctx]
             (assoc ctx :ship
                    (let [ship-id (get-in ctx [:request :params :id])]
                      (dbg ship-id)
                      (if (= ship-id "new")
                        (new-ship)
                        (view-ship
                         (entity-gateway db)
                         (Long/parseLong ship-id))))))

  :handle-ok (fn [ctx]
               (dbg (ship-presenter/present-ship-show (:ship ctx)))))

(defresource ship-list [db]
  :available-media-types ["text/html"]
  :handle-ok (fn [req]
               (ship-presenter/present-ship-index
                (list-ships (entity-gateway db)))))
