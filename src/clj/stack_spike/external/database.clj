(ns stack-spike.external.database)

(defprotocol Database

  (create [this]
    "Create the database (if it doesn't already exist).")

  (destroy [this]
    "Destroy the database.")

  (load-schema [this schema]
    "Load a schema into the database.")

  (entity-gateway [this]
    "Create an entity gateway for this database."))
