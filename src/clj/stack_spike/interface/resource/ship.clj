(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [liberator.representation :refer [ring-response]]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship create-ship update-ship delete-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug spy]]))


(defn content [request]
  (:body request))



(defn ship []
  :available-media-types ["application/transit+json"]
  :allowed-methods [:get :put :delete]
  :exists? (fn [ctx]
             (assoc ctx ::ship
                    (let [ship-id (get-in ctx [:request :params :id])]
                      (if (= ship-id "new")
                        (new-ship)
                        (view-ship
                         (entity-gateway (get-in ctx [:request :db]))
                         (Long/parseLong ship-id))))))
  :put! (fn [ctx]
          (let [params (content (:request ctx))]
            {::id (update-ship (entity-gateway (get-in ctx [:request :db])) params)}))
  :delete! (fn [ctx]
             (let [id (get-in ctx [:request :params :id])]
               (delete-ship (entity-gateway (get-in ctx [:request :db])) id)) )
  :new? false
  :respond-with-entity? true
  :handle-ok (fn [ctx] (::ship ctx)))

(defresource ship-list []
  :available-media-types ["application/transit+json"]
  :allowed-methods [:get :post]
  :handle-ok (fn [ctx] (list-ships (entity-gateway (get-in ctx [:request :db]))))
  :post! (fn [ctx]
           (let [params (content (:request ctx))
                 ship-id (create-ship (entity-gateway (get-in ctx [:request :db])) params)]
             {::id ship-id
              ::ship (view-ship
                         (entity-gateway (get-in ctx [:request :db]))
                         ship-id)}))

   :handle-created (fn [ctx] (::ship ctx)))
