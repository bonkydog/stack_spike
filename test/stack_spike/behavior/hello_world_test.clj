(ns stack-spike.behavior.hello-world-test
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            [stack-spike.external.browser :refer [visit assert-text-present]]
            [stack-spike.utility.debug :refer [dbg]]))

(use-fixtures :each
  (fn [t]
    (init-and-start-test-system)
    (t)
    (stop-test-system)))

(deftest feature-home-page
  (testing "visting the home page"
    (visit (:browser test-system) "/")
    (assert-text-present (:browser test-system) "Hello")))

(deftest failing-feature-home-page
  (testing "visting the home page"
    (visit (:browser test-system) "/")
    (assert-text-present (:browser test-system) "Smello")))
