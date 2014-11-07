(ns stack-spike.use-case.list-ships
  (:require [stack-spike.use-case.entity-gateway :as eg]))


(defn list-ships [entity-gateway]
  (eg/retrieve-entities entity-gateway :ship))
