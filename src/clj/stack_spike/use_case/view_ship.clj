(ns stack-spike.use-case.view-ship
  (:require [stack-spike.use-case.entity-gateway :as eg]))

(defn view-ship [entity-gateway id]
  (eg/retrieve-entity entity-gateway id))

(defn new-ship []
  {:name ""})
