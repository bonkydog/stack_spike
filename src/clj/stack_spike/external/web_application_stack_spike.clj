(ns stack-spike.external.web-application-stack-spike
  (:require [stack-spike.external.web-application :as web-application]
            [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [liberator.dev :refer [wrap-trace]]
            [bidi.bidi :as b]
            [datomic.api :as d]
            (stack-spike.interface.resource
             [ship :as ship]
             [home :as home])))
(def routes
  ["/" {"" :home
        "ships" :ships
        ["ships/" :id] :ship}])

(defn make-resources [db]
  {:home (home/home db)
   :ship (ship/ship db)
   :ships (ship/ship-list db)})

(defrecord WebApplicationStackSpike [db handler resources]

  component/Lifecycle

  (start [component]
    (assoc component
      :handler (web-application/make-handler component)))

  (stop [component]
    (dissoc component :handler))

  web-application/WebApplication

  (make-handler [this]
    (->  (b/make-handler routes (make-resources (:db this)))
         (wrap-trace :header :ui)
         wrap-stacktrace-web)))


(defn new-web-application-stack-spike []
  (map->WebApplicationStackSpike {}))
