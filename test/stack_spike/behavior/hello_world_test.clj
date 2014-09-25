(ns stack-spike.behavior.hello-world-test
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [stack-spike.core :refer [application]]
            [stack-spike.external.browser :refer [new-browser]]
            [com.stuartsierra.component :as component]))

(def test-system nil)

(defn test-db-uri []
  (str "datomic:mem://stack-spike-test-" (.getId (Thread/currentThread))))

(defn init-and-start-test-system []
  (alter-var-root #'test-system
                  (constantly (assoc (application 0 (test-db-uri)) :browser (new-browser) )))
  (alter-var-root #'test-system component/start))

(defn stop-test-system []
    (alter-var-root #'test-system component/stop))

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
