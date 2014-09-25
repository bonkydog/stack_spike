(ns stack-spike.external.jetty
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [port server handler]
  component/Lifecycle
  (start [component]
    (let [server (run-jetty (:handler handler) {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (when server
      (.stop server)
      component)))

(defn new-web-server
  "Creates a new Jetty web server component."
  [port]
  (map->WebServer {:port port}))

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
