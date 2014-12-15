(ns stack-spike.external.entity-gateway-datomic
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [datomic.api :as d :refer [db q]]))

(defn- conn [this]
  (d/connect (get-in this [:db :uri])))

(defn- dbv [this]
  (db (conn this)))

(defrecord EntityGatewayDatomic [db]
  eg/EntityGateway

  (retrieve-entity [this id]
    (d/touch (d/entity (dbv this) id)))

  (store-entity [this entity]
    @(d/transact (conn this) entity))

  (retrieve-entities [this type]
    (let [name-attribute (str type "/name")]
      (->> (q '[:find ?e :in $ ?attr :where [?e ?attr]] (dbv this) name-attribute)
           (map first)
           sort
           (map #(d/entity (dbv this) %))
           (map d/touch)))))

(defn new-entity-gateway-datomic [db]
  (map->EntityGatewayDatomic {:db db}))

(def schema
  [{:db/id #db/id[:db.part/db]
   :db/ident :ship/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "An ship's name"
   :db.install/_attribute :db.part/db}])
