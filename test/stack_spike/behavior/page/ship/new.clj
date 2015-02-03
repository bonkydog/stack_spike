(ns stack-spike.behavior.page.ship.new
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]

            [clj-webdriver.taxi :refer :all]))

(defn path []
  "/ships/new")

(defn visit []
  (browser/visit (path)))

(defn arrive []
  (browser/arrive (path)))

(defn create-test-ship []
  (quick-fill-submit {"#name" "Test Ship"}
                     {"input[type='submit']" click}))
