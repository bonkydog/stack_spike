(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [liberator.representation :refer [ring-response]]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship create-ship update-ship delete-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug spy]]))



(defresource ship-list []
  :available-media-types ["application/transit+json"]
  :allowed-methods [:get]
  :handle-ok (fn [ctx] (list-ships (entity-gateway (get-in ctx [:request :db])))))


(defresource action []
  :available-media-types ["application/transit+json"]
  :allowed-methods [:post]
  :post! (fn [ctx]
           (let [[action arg] (get-in ctx [:request :body])
                 eg (entity-gateway (get-in ctx [:request :db]))]
             {::action-result (case action
                               :create-ship (create-ship eg arg)
                               :update-ship (update-ship eg arg)
                               :delete-ship (delete-ship eg (:id arg)))}))
  :handle-created (fn [ctx] {:result (::action-result ctx)}))
