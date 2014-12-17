(ns stack-spike.behavior.page.ship.index
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]
            [clj-webdriver.taxi :refer :all]))

(def path "/ships")

(defn visit []
  (browser/visit path))

(defn arrive []
  (browser/arrive path))

(defn assert-no-ships-listed []
  (is (not (exists? "a.ship"))))

(defn assert-test-ship-listed []
  (is  (find-element {:css "a.ship" :text "Test Ship"})))
