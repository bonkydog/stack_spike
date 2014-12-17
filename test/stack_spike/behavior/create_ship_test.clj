(ns stack-spike.behavior.create-ship-test
  (:require [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            (stack-spike.behavior.page.ship
             [index :as ship.index]
             [new :as ship.new])))

(use-fixtures :once integration-test-fixture)

(deftest create-ship-test
  (ship.index/visit)
  (ship.index/assert-no-ships-listed)
  (ship.new/visit)
  (ship.new/create-test-ship)
  (ship.index/arrive)
  (ship.index/assert-test-ship-listed))
