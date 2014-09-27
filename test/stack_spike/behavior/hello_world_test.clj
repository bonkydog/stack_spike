(ns stack-spike.behavior.hello-world-test
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            [stack-spike.external.browser :refer [visit]]
            [stack-spike.utility.debug :refer [dbg]]))

(use-fixtures :each
  (fn [t]
    (init-and-start-test-system)
    (t)
    (stop-test-system)))

(defn assert-text-present [expected-content]
  (let [actual-content (text (find-element {:css "body"}))]
    (is (<= 0
           (.indexOf
            actual-content
            expected-content))
	    (str "expected to see \"" expected-content "\" in \"" actual-content "\""))))

(deftest feature-home-page
  (testing "visting the home page"
    (visit (:browser test-system) "/")
    (assert-text-present "Hello")))

(deftest failing-feature-home-page
  (testing "visting the home page"
    (visit (:browser test-system) "/")
    (assert-text-present "Smello")))
