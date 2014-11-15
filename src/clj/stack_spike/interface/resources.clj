(ns stack-spike.interface.resources
  (:require (stack-spike.interface.resource
             [ship :as ship]
             [home :as home])))

(defn resources [db]
  {:home (home/home db)
   :ship (ship/ship db)
   :ships (ship/ship-list db)})
