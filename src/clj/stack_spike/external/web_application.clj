(ns stack-spike.external.web-application)

(defprotocol WebApplication

  (handler [this]
    "Returns a ring handler for the application."))

