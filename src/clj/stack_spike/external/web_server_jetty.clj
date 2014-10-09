(ns stack-spike.external.web-server-jetty
  (:require [com.stuartsierra.component :as component]
            [stack-spike.external.web-server :as web-server]
            [ring.adapter.jetty :refer [run-jetty]]
            [stack-spike.utility.debug :refer [dbg]]))

(defrecord WebServerJetty [port server handler]

  component/Lifecycle

  (start [component]
    (let [server (run-jetty (:handler handler) {:port port :join? false})]
      (assoc component :server server)))

  (stop [component]
    (when (:server component)
      (.stop (:server component))
      (dissoc component :server)))

  web-server/WebServer

  (local-port [this]
    (.getLocalPort (first (.getConnectors (:server this)))))

  (local-host [_]
    (.getHostName (java.net.InetAddress/getLocalHost)))

  (root-url [this]
    (str "http://" (web-server/local-host this) ":" (web-server/local-port this) "/")))

(defn new-web-server-jetty
  "Creates a new (Jetty) web server component."
  [port]
  (map->WebServerJetty {:port port}))
