(ns stack-spike.core
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [bidi.bidi :refer (make-handler)]
            [liberator.core :refer [defresource]]
            [environ.core :refer [env]]
            (stack-spike.external
             [web-server-jetty :refer [new-web-server-jetty]]
             [database-datomic :refer [new-database-datomic]]
             (handler :refer [new-handler])))
  (:gen-class :main true))

(def schema
  [{:db/id #db/id[:db.part/db]
   :db/ident :exercise/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "An exercise's name"
   :db.install/_attribute :db.part/db}])

(defn application [http-port datomic-uri]
  (component/system-map
   :datomic-db (new-database-datomic datomic-uri)
   :handler (component/using (new-handler) [:datomic-db])
   :web (component/using (new-web-server-jetty http-port) [:handler])))

(defn -main
  "Run the application."
  [& args]
  (component/start (application (env :http-port) (env :datomic-uri))))
