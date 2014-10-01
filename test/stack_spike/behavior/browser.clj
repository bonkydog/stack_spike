(ns stack-spike.behavior.browser
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [stack-spike.external.web :as web]
            [clojure.string :as s]))

(defn browser-session-fixture [t]
  (let [driver (new-driver {:browser :firefox})]
    (set-driver! driver)
    (implicit-wait driver 3000)
    ;;(.addShutdownHook (Runtime/getRuntime) (Thread. #(quit driver)))
    (t)
    (quit driver)
    ))

(defn path->url [sys path]
  (str (web/root-url (:web sys)) (s/replace-first path #"^/" "")))

(defn visit [sys path]
  (to  (path->url sys path)))

(defn arrive [sys path]
  (let [expected-url (path->url sys path)]
    (try
      (wait-until (fn [] (= expected-url (current-url)) ))
      (catch org.openqa.selenium.TimeoutException e
        (let [actual-url (current-url)]
          ;; we assert instead of calling "is" here, because if we're on the wrong page we may as well quit.
          (assert (= expected-url actual-url) (str "Expected to arrive at " expected-url ".  After waiting, current url was still " actual-url)))))))

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
