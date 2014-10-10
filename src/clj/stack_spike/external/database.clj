(ns stack-spike.external.database)

(defprotocol Database

  (create [this]
    "Create the database (if it doesn't already exist).")

  (destroy [this]
    "Destroy the database.")

  (load-schema [this schema]
    "Load a schema into the database.")
  
  (retrieve-entity [this id]
    "Fetch the entity with the requested id.")

  (store-entity [this entity]
    "Store the entity in the database.")
  
  (list-entities [this type]
    "Fetch all entities of the requested type."))
