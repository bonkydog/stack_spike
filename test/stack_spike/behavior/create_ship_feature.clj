(ns stack-spike.behavior.create-ship-feature
  (:require [clojure.test :refer :all]
            (stack-spike.behavior.page.ship
             [index :as ship.index]
             [new :as ship.new])))

(deftest create-ship
  (ship.index/visit)
  (ship.index/assert-no-ships-listed)
  (ship.new/create-test-ship)
  (ship.index/arrive)
  (ship.index/assert-test-ship-listed))
