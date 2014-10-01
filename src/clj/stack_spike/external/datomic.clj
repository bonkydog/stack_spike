(ns stack-spike.external.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(defrecord Datomic [uri db]
  component/Lifecycle
  (start [component]
    (d/create-database uri)
    (assoc component :uri uri))
  (stop [component]
    (dissoc component :uri)))

(defn new-datomic-db [uri]
  (map->Datomic {:uri uri}))
