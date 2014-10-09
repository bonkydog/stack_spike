(ns stack-spike.behavior.browser
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.string :as s]
            [clojure.test :refer :all]
            [stack-spike.external.web-server :as web]))

(def browser :firefox)
(def seconds-to-wait 500)

(defn setup-browser-session! []
  (try
    (implicit-wait *driver* seconds-to-wait)
    (catch Exception e
      (set-driver! (new-driver {:browser browser}))))
  *driver*)

(defn browser-session-fixture [t]
  (setup-browser-session!)
  (t))

(defn path->url [sys path]
  (str (web/root-url (:web sys)) (s/replace-first path #"^/" "")))

(defn visit [sys path]
  (to  (path->url sys path)))

(defn arrive [sys path]
  (let [expected-url (path->url sys path)]
    (try
      (wait-until (fn [] (= expected-url (current-url)) ) seconds-to-wait)
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
