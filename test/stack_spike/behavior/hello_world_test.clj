(ns stack-spike.behavior.hello-world-test
  (:require [clojure.test :refer :all]
            [stack-spike.core-test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [stack-spike.core :refer [application]]
            [stack-spike.external.browser :refer [new-browser]]
            [com.stuartsierra.component :as component]))




(use-fixtures :each
  (fn [t]
    (init-and-start-test-system)
    (t)
    (stop-test-system)))

(defn test-url [path]
  (str "http://localhost:"
       (stack-spike.external.jetty/local-port (:web test-system))
       path))

(defn visit [path]
  (to (test-url path)))

(defn assert-text-present [expected-content]
  (let [actual-content (text (find-element {:css "body"}))]
    (is (<= 0
           (.indexOf
            actual-content
            expected-content))
	    (str "expected to see \"" expected-content "\" in \"" actual-content "\""))))

(deftest feature-home-page
  (testing "visting the home page"
    (visit "/")
    (assert-text-present "Hello")))

(deftest failing-feature-home-page
  (testing "visting the home page"
    (visit "/")
    (assert-text-present "Smello")))
