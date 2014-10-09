(ns stack-spike.external.database)

(defprotocol Database

  (entity [this id]
    "Fetch the enitity with the requested id.")

  (list-entities [this type]
    "Fetch all entities of the requested type."))
