(ns stack-spike.model.spaceship)

(defrecord Spaceship [name])

(defn new-spaceship [name]
  (->Spaceship name))
