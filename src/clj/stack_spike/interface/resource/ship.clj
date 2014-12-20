(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [liberator.representation :refer [ring-response]]
            [stack-spike.interface.presenter.ship :as ship-presenter]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship create-ship update-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug]]))

(defresource ship [db root-url]
  :available-media-types ["text/html"]
  :allowed-methods [:get :put]
  :exists? (fn [ctx]
             (assoc ctx ::ship
                    (let [ship-id (get-in ctx [:request :params :id])]
                      (if (= ship-id "new")
                        (new-ship)
                        (view-ship
                         (entity-gateway db)
                         (Long/parseLong ship-id))))))
  :put! (fn [ctx]
          (let [params (get-in ctx [:request :params])]
            {::id (update-ship (entity-gateway db) params)}))
  :new? false
  :respond-with-entity? true
  :handle-ok (fn [ctx]
               (if (= :put (get-in ctx [:request :request-method]))
                 (ring-response {:status  303
                                 :headers {"Location" (r/url-for root-url :ships)}
                                 :body    ""})
                 (ship-presenter/present-ship-show (::ship ctx)))))

(defresource ship-list [db root-url]
  :available-media-types ["text/html"]
  :allowed-methods [:get :post]
  :handle-ok (fn [req]
               (ship-presenter/present-ship-index
                (list-ships (entity-gateway db))))
  :post! (fn [ctx]
           (let [params (get-in ctx [:request :params])]
             {::id (create-ship (entity-gateway db) params)}))

  :post-redirect? (fn [ctx] {:location (r/url-for root-url :ships)}))
