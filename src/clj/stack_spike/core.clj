(ns stack-spike.core
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [liberator.core :refer [defresource]]
            [environ.core :refer [env]]
            (stack-spike.external
             [url :refer [local-host-name]]
             [web-server-jetty :refer [new-web-server-jetty]]
             [database-datomic :refer [new-database-datomic]]
             (web-application-stack-spike :refer [new-web-application-stack-spike])))
  (:gen-class :main true))

(defn application [host-name http-port datomic-uri]
  (component/system-map
   :db (new-database-datomic datomic-uri)
   :app (component/using (new-web-application-stack-spike host-name http-port) [:db])
   :web (component/using (new-web-server-jetty http-port) [:app])))

(defn -main
  "Run the application."
  [& args]
  (component/start (application (local-host-name) (env :http-port) (env :datomic-uri))))
