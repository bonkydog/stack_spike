(ns stack-spike.external.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]
            [stack-spike.utility.debug :refer [dbg]]))

(defrecord Web [port server handler]
  component/Lifecycle
  (start [component]
    (let [server (run-jetty (:handler handler) {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (when (:server component)
      (.stop (:server component))
      (dissoc component :server))))

(defn new-web
  "Creates a new (Jetty) web server component."
  [port]
  (map->Web {:port port}))

(defn local-port
  "Returns the port on which the web server is listening."
  [web-server]
  (.getLocalPort (first (.getConnectors (:server web-server)))))

(defn local-host
  "Returns this machine's host name."
  []
  (.getHostName (java.net.InetAddress/getLocalHost)))

(defn root-url
  "Returns the URL of the root for the website."
  [web-server]
  (str "http://" (local-host) ":" (local-port web-server) "/"))
