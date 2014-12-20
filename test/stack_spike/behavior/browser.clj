(ns stack-spike.behavior.browser
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.string :as s]
            [clojure.test :refer :all]
            [stack-spike.core-test :refer [path->url seconds-to-wait]]
            [stack-spike.external.url :refer [local-root-url]]
            [stack-spike.utility.debug :refer [dbg]]))



(defn visit [path]
  (to (path->url path)))

(defn arrive [path]
  (let [expected-url (path->url path)]
    (try
      (wait-until (fn [] (= expected-url (current-url)) ) seconds-to-wait)
      (catch org.openqa.selenium.TimeoutException e
        (let [actual-url (current-url)]
          ;; we assert instead of calling "is" here, because if we're on
          ;; the wrong page we may as well quit.
          (assert (= expected-url actual-url)
                  (str "Expected to arrive at " expected-url ".  "
                       "After waiting, current url was still " actual-url)))))))

(defn assert-text-present [expected-content]
  (let [actual-content (text (find-element  {:css "body"}))]
    (is (<= 0
            (.indexOf
             actual-content
             expected-content))
        (str "Expected to see \"" expected-content "\" in \"" actual-content "\"."))))

(defn assert-text-not-present [expected-absent-content]
  (let [actual-content (text (find-element  {:css "body"}))]
    (is (> 0
            (.indexOf
             actual-content
             expected-absent-content))
        (str "Expected not to see \"" expected-absent-content "\" in \"" actual-content "\"."))))
