(ns stack-spike.external.entity-gateway-datomic
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [datomic.api :as d :refer [db q]]
            [clojure.tools.logging :refer [spy]]
            [clojure.walk]))

(defn- conn [this]
  (d/connect (get-in this [:db :uri])))

(defn- dbv [this]
  (db (conn this)))

(defn- normalize-id [id]
  (if (string? id)
    (Long/parseLong id)
    id))

(defn entity->map [e]
  (assoc  (into {} (d/touch e)) :db/id (:db/id e)))

(defn map->tx-data [m]
  (let [model (clojure.walk/keywordize-keys m)
        id (normalize-id (:db/id model))]
    (assoc
     model
     :db/id
     (or id #db/id[:db.part/user]))))

(defrecord EntityGatewayDatomic [db]
  eg/EntityGateway

  (retrieve-entity [this id]
    (entity->map (d/entity (dbv this) id)))

  (store-entity [this entity]
    (spy entity)
    (let [tx-data (map->tx-data entity)
          result @(d/transact (conn this) [(spy tx-data)])
          id (or (d/resolve-tempid (:db-after result) (:tempids result) (:db/id tx-data))
                  (:db/id tx-data))]
      (entity->map (d/entity (:db-after result) id))))

  (retrieve-entities [this type]
    (let [name-attribute (str type "/name")]
      (->> (q '[:find ?e
                :in $ ?attr
                :where [?e ?attr]] (dbv this) name-attribute)
           (map first)
           sort
           (map #(d/entity (dbv this) %))
           (map entity->map)
           (mapcat #(vector (:db/id %) %))
           (apply sorted-map))))
  (delete-entity [this id]
    @(d/transact (conn this) [[:db.fn/retractEntity (normalize-id id)]])
    true))

(defn new-entity-gateway-datomic [db]
  (map->EntityGatewayDatomic {:db db}))

(def schema
  [{:db/id #db/id[:db.part/db]
   :db/ident :ship/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "An ship's name"
   :db.install/_attribute :db.part/db}])
