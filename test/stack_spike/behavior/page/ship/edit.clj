(ns stack-spike.behavior.page.ship.edit
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]
            [clj-webdriver.taxi :refer :all]))

(defn path [ship-id]
  (str "/ships/" ship-id))

(defn visit [ship-id]
  (browser/visit (path ship-id)))

(defn arrive [ship-id]
  (browser/arrive (path ship-id)))

(defn update-test-ship []
  (clear "#name")
  (quick-fill-submit {"#name" "Different Name"}
                     {"input[type='submit']" click}))
