(ns stack-spike.behavior.ship-crud-test
  (:require [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            (stack-spike.behavior.page.ship
             [index :as ship.index]
             [new :as ship.new]
             [edit :as ship.edit])))

(use-fixtures :once integration-test-fixture)

(deftest ship-crud-test
  (ship.index/visit)
  (ship.index/assert-no-ships-listed)
  (ship.index/new-ship)
  (ship.new/create-test-ship)
  (ship.index/arrive)
  (ship.index/assert-test-ship-listed)
  (ship.index/assert-exactly-one-ship-listed)
  (let [ship-id (ship.index/id-of-first-ship)]
    (ship.index/edit-ship ship-id)
    (ship.edit/arrive ship-id)
    (ship.edit/update-test-ship)
    (ship.index/arrive)
    (ship.index/assert-test-ship-changed)
    (ship.index/assert-exactly-one-ship-listed)
    #_(ship.index/delete-ship ship-id)
    #_(ship.index/assert-no-ships-listed)))
