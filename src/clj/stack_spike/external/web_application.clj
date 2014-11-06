(ns stack-spike.external.web-application)

(defprotocol WebApplication

  (make-handler [this]
    "Builds a ring handler for the application."))

