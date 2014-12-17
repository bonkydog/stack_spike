(ns stack-spike.external.entity-gateway-datomic
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [datomic.api :as d :refer [db q]]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.walk]))

(defn- conn [this]
  (d/connect (get-in this [:db :uri])))

(defn- dbv [this]
  (db (conn this)))

(defn entity->map [e]
  (assoc  (into {} (d/touch e)) :db/id (:db/id e)))

(defn map->tx-data [m]
  (let [type "ship"
        model (clojure.walk/keywordize-keys m)]
    (assoc
     (into {} (map (fn [[k v]] [(keyword "ship" (name k)) v]) (dissoc model :id)))
     :db/id
     (or (:id model) #db/id[:db.part/user]))))

(defrecord EntityGatewayDatomic [db]
  eg/EntityGateway

  (retrieve-entity [this id]
    (entity->map (d/entity (dbv this) id)))

  (store-entity [this entity]
    (let [tx-data (map->tx-data entity)
          result @(d/transact (conn this) [(dbg tx-data)])]
      (or (d/resolve-tempid (dbg (:db-after result)) (dbg (:tempids result)) (dbg (:db/id tx-data)))
          (:db/id tx-data))))

  (retrieve-entities [this type]
    (let [name-attribute (str type "/name")]
      (->> (q '[:find ?e
                :in $ ?attr
                :where [?e ?attr]] (dbv this) name-attribute)
           (map first)
           sort
           (map #(d/entity (dbv this) %))
           (map entity->map)))))

(defn new-entity-gateway-datomic [db]
  (map->EntityGatewayDatomic {:db db}))

(def schema
  [{:db/id #db/id[:db.part/db]
   :db/ident :ship/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "An ship's name"
   :db.install/_attribute :db.part/db}])
