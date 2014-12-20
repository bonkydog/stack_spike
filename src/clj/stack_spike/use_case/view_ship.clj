(ns stack-spike.use-case.view-ship
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [clojure.walk :refer [keywordize-keys]]
            [stack-spike.model.spaceship :refer :all]))

(defn view-ship [entity-gateway id]
  (eg/retrieve-entity entity-gateway id))

(defn new-ship []
  {:name ""})

(defn create-ship [entity-gateway params]
  (eg/store-entity entity-gateway (map->Spaceship (select-keys (keywordize-keys params) [:name]))))

(defn update-ship [entity-gateway params]
  (eg/store-entity entity-gateway (map->Spaceship (select-keys (keywordize-keys params) [:id :name]))))
