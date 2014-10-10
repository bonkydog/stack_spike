(ns stack-spike.external.database-datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d :refer [db q]]
            [stack-spike.external.database :as database]
            [stack-spike.utility.debug :refer [dbg]]))

(defn- conn [this]
  (d/connect (:uri this)))

(defn- dbv [this]
  (db (conn this)))

(defrecord DatabaseDatomic [uri db]

  component/Lifecycle

  (start [component]
    (d/create-database uri)
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
  
  (retrieve-entity [this id]
    (d/entity (dbv this) id))

  (store-entity [this entity]
    @(d/transact (conn this) entity))
  
  (list-entities [this type]
    (let [name-attribute (keyword (str type "/name"))]
      (->> (q '[:find ?e :in $ ?attr :where [?e ?attr]] (dbv this) name-attribute)
           (map first)
           sort
           (map #(d/entity (dbv this) %))
           (map d/touch)))))

(defn new-database-datomic [uri]
  (map->DatabaseDatomic {:uri uri}))
