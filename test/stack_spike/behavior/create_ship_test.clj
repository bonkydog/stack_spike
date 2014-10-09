(ns stack-spike.behavior.create-ship-test
  (:require [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            [stack-spike.behavior.browser :refer [browser-session-fixture]]
            (stack-spike.behavior.page.ship
             [index :as ship.index]
             [new :as ship.new])))

(use-fixtures :once browser-session-fixture)

(defsystest create-ship [sys]
  (ship.index/visit sys)
  (ship.index/assert-no-ships-listed)
  (ship.new/visit sys)
  (ship.new/create-test-ship)
  (ship.index/arrive sys)
  (ship.index/assert-test-ship-listed))
