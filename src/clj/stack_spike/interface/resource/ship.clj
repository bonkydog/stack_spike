(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.presenter.ship :as ship-presenter]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship create-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug]]))

(defresource ship [db root-url]
  :available-media-types ["text/html"]

  :exists? (fn [ctx]
             (assoc ctx ::ship
                    (let [ship-id (get-in ctx [:request :params :id])]
                      (if (= ship-id "new")
                        (new-ship)
                        (view-ship
                         (entity-gateway db)
                         (Long/parseLong ship-id))))))

  :handle-ok (fn [ctx]
               (ship-presenter/present-ship-show (::ship ctx))))

(defresource ship-list [db root-url]
  :available-media-types ["text/html"]
  :allowed-methods [:post :get]
  :handle-ok (fn [req]
               (ship-presenter/present-ship-index
                (list-ships (entity-gateway db))))
  :post! (fn [ctx]
           (let [params (get-in ctx [:request :params])]
             {::id (create-ship (entity-gateway db) params)}))
  :post-redirect? (fn [ctx] {:location (r/url-for root-url :ships)}))
