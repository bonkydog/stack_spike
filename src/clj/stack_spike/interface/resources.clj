(ns stack-spike.interface.resources
  (:require [bidi.ring :as r]
            [stack-spike.utility.debug :refer [dbg]]
            (stack-spike.interface.resource
             [ship :as ship]
             [home :as home])))

(def resources 
  {:home home/home
   :ships-api ship/ship-list
   :action ship/action
   :om home/om})
