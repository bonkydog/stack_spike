(ns stack-spike.use-case.view-ship
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [clojure.walk :refer [keywordize-keys]]
            [stack-spike.model.spaceship :refer :all]))

(defn view-ship [entity-gateway id]
  (eg/retrieve-entity entity-gateway id))

(defn create-ship [entity-gateway params]
  (eg/store-entity entity-gateway (select-keys params [:ship/name])))

(defn update-ship [entity-gateway params]
  (eg/store-entity entity-gateway (select-keys params [:db/id :shipname])))

(defn delete-ship [entity-gateway id]
  (eg/delete-entity entity-gateway id))
