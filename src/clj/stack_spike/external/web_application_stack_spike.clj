(ns stack-spike.external.web-application-stack-spike
  (:require [stack-spike.external.web-application :as web-application]
            [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [ring.middleware.params :refer [wrap-params]]
            [liberator.dev :refer [wrap-trace]]
            [bidi.bidi :as b]
            [datomic.api :as d]
            [stack-spike.interface.routes :refer [routes]]
            [stack-spike.interface.resources :refer [resources]]
            [stack-spike.external.url :refer [local-root-url]]))

(defrecord WebApplicationStackSpike [db host-name port handler]

  component/Lifecycle

  (start [component]
    (assoc component
      :handler (web-application/make-handler component)))

  (stop [component]
    (dissoc component :handler))

  web-application/WebApplication

  (make-handler [this]
    (->  (b/make-handler
          stack-spike.interface.routes/routes
          (stack-spike.interface.resources/resources
           (:db this)
           (str (local-root-url port))))
      wrap-params
      (wrap-trace :header :ui)
      wrap-stacktrace-web)))


(defn new-web-application-stack-spike [host-name port]
  (map->WebApplicationStackSpike {:host-name host-name, :port port}))
