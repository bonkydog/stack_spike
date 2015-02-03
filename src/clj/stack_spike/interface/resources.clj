(ns stack-spike.interface.resources
  (:require [bidi.ring :as r]
            [stack-spike.utility.debug :refer [dbg]]
            (stack-spike.interface.resource
             [ship :as ship]
             [home :as home])))

(defn resources [db]
  {:home (home/home db)
   :ship (ship/ship db)
   :ships (ship/ship-list db)
   :om (home/om db)})
