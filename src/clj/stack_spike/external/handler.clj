(ns stack-spike.external.handler
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [liberator.dev :refer [wrap-trace]]
            [bidi.bidi :refer (make-handler)]
            [datomic.api :as d]
            (stack-spike.external.resource
             [ship :as ship]
             [home :as home])))


(defn wrap-datomic-conn [handler datomic-uri]
  (fn [request]
    (handler (assoc request :conn (d/connect datomic-uri)))))

(def routes
  ["/" {"" home/home
        "ships" ship/ship-list
        ["ships/" :id] ship/ship}])

(defrecord Handler [datomic-db handler]
  component/Lifecycle
  (start [component]
    (assoc component
      :handler (->  (make-handler routes)
                    (wrap-datomic-conn (:uri datomic-db))
                    (wrap-trace :header :ui)
                    wrap-stacktrace-web)))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler []
  (map->Handler {}))
