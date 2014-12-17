(ns stack-spike.external.web-server-jetty
  (:require [com.stuartsierra.component :as component]
            [stack-spike.external.web-server :as web-server]
            [ring.adapter.jetty :refer [run-jetty]]
            [stack-spike.external.url :refer [local-host-name]]
            [stack-spike.utility.debug :refer [dbg]]))

(defn configurator
  [jetty]
  (doseq [connector (.getConnectors jetty)]
    (.setResponseHeaderSize connector 16384)))

(defrecord WebServerJetty [port server app]

  component/Lifecycle

  (start [component]
    (let [server (run-jetty (:handler app) {:port port
                                            :join? false
                                            :configurator configurator})]
      (assoc component :server server)))

  (stop [component]
    (when (:server component)
      (.stop (:server component))
      (.join (:server component))
      (dissoc component :server)))

  web-server/WebServer

  (root-url [this]
    (str "http://" (local-host-name) ":" (:port this) "/")))

(defn new-web-server-jetty
  "Creates a new (Jetty) web server component."
  [port]
  (map->WebServerJetty {:port port}))
