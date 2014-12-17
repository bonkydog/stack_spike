(ns stack-spike.behavior.page.ship.new
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]
            [clj-webdriver.taxi :refer :all]))

(def path "ships/new")

(defn visit [sys]
  (browser/visit sys path))

(defn arrive [sys]
  (browser/arrive sys path))

(defn create-test-ship []
  (quick-fill-submit {"#name" "Test Ship"}
                     {"input[type='submit']" click}))
