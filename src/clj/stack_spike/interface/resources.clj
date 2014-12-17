(ns stack-spike.interface.resources
  (:require (stack-spike.interface.resource
             [ship :as ship]
             [home :as home])))

(defn resources [db root-url]
  {:home (home/home db root-url)
   :ship (ship/ship db root-url)
   :ships (ship/ship-list db root-url)})
