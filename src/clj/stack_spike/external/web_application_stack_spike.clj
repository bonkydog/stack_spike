(ns stack-spike.external.web-application-stack-spike
  (:require [stack-spike.external.web-application :as web-application]
            [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [liberator.dev :refer [wrap-trace]]
            [bidi.bidi :refer (make-handler)]
            [datomic.api :as d]
            (stack-spike.external.resource
             [ship :as ship]
             [home :as home])))


(defn routes [db]
  ["/" {"" (home/home db)
        "ships" (ship/ship-list db)
        ["ships/" :id]  (ship/ship db)}])

(defrecord WebApplicationStackSpike [db handler]

  component/Lifecycle

  (start [component]
    (assoc component
      :handler (web-application/handler component)))

  (stop [component]
    (dissoc component :handler))

  web-application/WebApplication

  (handler [this]
    (->  (make-handler (routes (:db this)))
         (wrap-trace :header :ui)
         wrap-stacktrace-web)))


(defn new-web-application-stack-spike []
  (map->WebApplicationStackSpike {}))
