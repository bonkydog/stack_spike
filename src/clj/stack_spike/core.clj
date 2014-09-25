(ns stack-spike.core
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [bidi.bidi :refer (make-handler)]
            [liberator.core :refer [defresource]]
            [environ.core :refer [env]]
            (stack-spike.external
             [jetty :refer [new-jetty]]
             [datomic :refer [new-datomic-db]]
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
   :datomic-db (new-datomic-db datomic-uri)
   :handler (component/using (new-handler) [:datomic-db])
   :web (component/using (new-jetty http-port) [:handler])))

(defn -main
  "Run the application."
  [& args]
  (component/start (application (env :http-port) (env :datomic-uri) )))
