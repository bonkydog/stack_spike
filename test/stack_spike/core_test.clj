(ns stack-spike.core-test
  (:require [com.stuartsierra.component :as component]
            [stack-spike.core :refer :all]
            [stack-spike.external.browser :refer [new-browser]]))

(def test-system nil)

(defn test-application [http-port datomic-uri]
  (assoc (application http-port datomic-uri) :browser (new-browser)))

(defn test-db-uri []
  (str "datomic:mem://stack-spike-test-" (.getId (Thread/currentThread))))

(defn init-and-start-test-system []
  (alter-var-root #'test-system
                  (constantly (test-application 0 (test-db-uri))))
  (alter-var-root #'test-system component/start))

(defn stop-test-system []
    (alter-var-root #'test-system component/stop))
