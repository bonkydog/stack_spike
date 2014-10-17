(ns stack-spike.use-case.view-ship
  (:require [stack-spike.use-case.entity-gateway :as eg]
            [stack-spike.utility.debug :refer [dbg]]))

(defn view-ship [entity-gateway id presenter]
  (presenter (dbg (eg/retrieve-entity entity-gateway id))))

