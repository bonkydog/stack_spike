(ns stack-spike.behavior.page.ship.new
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]
            [stack-spike.core-test :refer [*test-om-interface*]]
            [clj-webdriver.taxi :refer :all]))

(defn path []
  (str (if *test-om-interface* "#" "" ))"#ships/new")

(defn visit []
  (browser/visit (path)))

(defn arrive []
  (browser/arrive (path)))

(defn create-test-ship []
  (quick-fill-submit {"#name" "Test Ship"}
                     {"input[type='submit']" click}))
