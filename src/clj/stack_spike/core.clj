(ns stack-spike.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as web]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [datomic.api :refer [db q] :as d]
            [bidi.bidi :refer (make-handler)]
            [liberator.core :refer [defresource]])
  (:gen-class :main true))

(def schema
  [{:db/id #db/id[:db.part/db]
   :db/ident :exercise/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "An exercise's name"
   :db.install/_attribute :db.part/db}])


(defn wrap-db [handler database]
  (fn [request]
    (handler (assoc request :conn (:conn database)))))

(defresource home
  :available-media-types ["text/plain"]
  :handle-ok (fn [ctx] (str "Hello, reloaded world! Here is my database connection: " (get-in ctx [:request :conn]))))

(def routes
  (make-handler ["/" home]))

(defrecord Application [handler database]
  component/Lifecycle
  (start [component]
    (assoc component :handler (-> handler
                                  (wrap-db database))))
  (stop [component]
    (assoc component :handler nil)))

(defn new-app [config]
  (map->Application config))

(defrecord Database [uri schema reset?]
  component/Lifecycle
  (start [component]
    (if reset? (d/delete-database uri))
    (d/create-database uri)
    (let [conn (d/connect uri)]
      (d/transact conn schema)
      (println "Connecting to database.")
      (assoc component :conn conn)))
  (stop [component]
    ;; Note that it is unnecessary to release the connection.
    ;; Please see http://docs.datomic.com/clojure/index.html#datomic.api/release
    (println "Dropping database connection.")
    (assoc component :conn nil)))

(defn new-database [config]
  (map->Database config))

(defrecord WebServer [app port join?]
  component/Lifecycle
  (start [component]
    (println "Starting web server.")
    (assoc component :web-server (web/run-jetty (:handler app) {:port port :join? join?})))
  (stop [component]
    (println "Stopping web server.")
    (when-let [server (:web-server component)] (.stop server))
    (assoc component :web-server nil)))

(defn new-web-server [config]
  (map->WebServer config))

(defn stack-spike-system [config]
  (component/system-map
   :database (new-database (:db config))
   :app (component/using (new-app (:app config))
                         [:database])
   :web-server (component/using
                (new-web-server (:web config))
                [:app])))

(def default-config {:web {:port 8080
                           :join true}
                     :db {:uri "datomic:dev://localhost:4334/stack-spike"
                          :reset? false
                          :schema schema}
                     :app {:handler routes}})

(defn -main
  "Run the application."
  [& args]
  (let [[port] args]
    (component/start (stack-spike-system default-config))))
