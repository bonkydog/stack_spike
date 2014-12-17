(ns stack-spike.external.url
  (:require [cemerick.url :as u]))

(import java.net.ServerSocket)

(defn unused-port
  "Pick an unused TCP port."
  []
  (let [socket (ServerSocket. 0)
        port (.getLocalPort socket)]
    (.close socket)
    port))

(defn local-host-name
 "Determine the local host name."
 []
 (.getHostName (java.net.InetAddress/getLocalHost)))

(defn local-root-url
  "Determine root url."
  [port]
  (-> (u/url (str "http://" (local-host-name)))
    (assoc :port port)
    str))
