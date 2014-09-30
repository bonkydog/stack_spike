(ns stack-spike.behavior.hello-world-test
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.test :refer :all]
            [clj-webdriver.taxi :as taxi]
            [stack-spike.core-test :refer :all]
            [stack-spike.behavior.browser :refer :all]
            [stack-spike.utility.debug :refer [dbg]]))

(use-fixtures :once browser-session-fixture)

(defsystest feature-home-page [sys]
  (testing "visting the home page"
    (visit sys "/")
    (assert-text-present "Hello")))

(defsystest failing-feature-home-page [sys]
  (testing "visting the home page"
    (visit sys "/")
    (assert-text-present "Smello")))
