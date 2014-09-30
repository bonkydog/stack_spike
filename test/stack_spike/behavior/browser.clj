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
    (quit driver)))

(defn visit [sys path]
  (to  (str (web/root-url (:web sys)) (s/replace-first path #"^/" ""))))

(defn assert-text-present [expected-content]
  (let [actual-content (text (find-element  {:css "body"}))]
    (is (<= 0
            (.indexOf
             actual-content
             expected-content))
        (str "expected to see \"" expected-content "\" in \"" actual-content "\""))))
