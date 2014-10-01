(ns stack-spike.external.handler
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [bidi.bidi :refer (make-handler)]
            [liberator.core :refer [defresource]]
            [datomic.api :as d]))


(defn wrap-datomic-conn [handler datomic-uri]
  (fn [request]
    (handler (assoc request :conn (d/connect datomic-uri)))))

(defresource home
  :available-media-types ["text/plain"]
  :handle-ok (fn [ctx]
               (str "Hello, world! Here is my database connection: "
                    (get-in ctx [:request :conn]))))

(def routes
  (make-handler ["/" home]))

(defrecord Handler [datomic-db handler]
  component/Lifecycle
  (start [component]
    (assoc component
      :handler (->  routes
                    (wrap-datomic-conn (:uri datomic-db))
                    wrap-stacktrace-web)))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler []
  (map->Handler {}))
