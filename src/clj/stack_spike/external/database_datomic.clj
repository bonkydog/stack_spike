(ns stack-spike.external.database-datomic
  (:require [com.stuartsierra.component :as component]
            [stack-spike.external.database :as database]
            [datomic.api :as d]))

(defrecord DatabaseDatomic [uri db]

  component/Lifecycle

  (start [component]
    (d/create-database uri)
    (assoc component :uri uri))

  (stop [component]
    (dissoc component :uri))

  database/Database

  (entity [this id]
    :STUB)

  (list-entities [this type]
    [:STUB]))

(defn new-database-datomic [uri]
  (map->DatabaseDatomic {:uri uri}))
