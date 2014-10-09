(ns stack-spike.external.web-server)

(defprotocol WebServer
  
  (local-port [this]
    "Returns the port on which the web server is listening.")

  (local-host [this]
    "Returns this machine's host name.")

  (root-url [this]
    "Returns the URL of the root for the website."))
