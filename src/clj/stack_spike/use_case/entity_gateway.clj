(ns stack-spike.use-case.entity-gateway)

(defprotocol EntityGateway
  (retrieve-entity [this id]
    "Retrieve the entity with the requested id.")

  (store-entity [this entity]
    "Store entity in the database.")

  (retrieve-entities [this type]
    "Retrieve all entities of the requested type.")

  (delete-entity [this id]
    "Deletes the entity with the requested id"))
