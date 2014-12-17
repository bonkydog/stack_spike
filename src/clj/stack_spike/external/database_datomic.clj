(ns stack-spike.external.database-datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d :refer [db q]]
            [stack-spike.external.database :as database]
            [stack-spike.external.entity-gateway-datomic :as gateway]
            [stack-spike.utility.debug :refer [dbg]]
            [stack-spike.external.entity-gateway-datomic :refer [new-entity-gateway-datomic]]))

(defn- conn [this]
  (d/connect (:uri this)))

(defn- dbv [this]
  (db (conn this)))

(defrecord DatabaseDatomic [uri db]

  component/Lifecycle

  (start [component]
    (d/create-database uri)
    (database/load-schema component gateway/schema)
    (assoc component :uri uri))

  (stop [component]
    (dissoc component :uri))

  database/Database

  (create [this]
    (d/create-database (:uri this)))

  (destroy [this]
    (d/delete-database (:uri this)))

  (load-schema [this schema]
    @(d/transact (conn this) schema))

  (entity-gateway [this]
    "Create an entity gateway for this database."
    (new-entity-gateway-datomic this)))

(defn new-database-datomic [uri]
  (map->DatabaseDatomic {:uri uri}))
