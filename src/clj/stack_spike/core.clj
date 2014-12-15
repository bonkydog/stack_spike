(ns stack-spike.core
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [bidi.bidi :refer (make-handler)]
            [liberator.core :refer [defresource]]
            [environ.core :refer [env]]
            [ring.component.jetty :refer [jetty-server]]
            (stack-spike.external
             [database-datomic :refer [new-database-datomic]]
             (web-application-stack-spike :refer [new-web-application-stack-spike])))
  (:gen-class :main true))

(defn application [http-port datomic-uri]
  (component/system-map
   :db (new-database-datomic datomic-uri)
   :app (component/using (new-web-application-stack-spike) [:db])
   :web (component/using (jetty-server {:port http-port}) [:app])))

(defn -main
  "Run the application."
  [& args]
  (component/start (application (env :http-port) (env :datomic-uri))))
