(ns stack-spike.core-test
  (:require [com.stuartsierra.component :as component]
            [stack-spike.core :refer :all]
            [stack-spike.external.browser :refer [new-browser]]))

(def test-system nil)


(defn test-db-uri []
  (str "datomic:mem://stack-spike-test-" (.getId (Thread/currentThread))))

(defn init-and-start-test-system []
  (alter-var-root #'test-system
                  (constantly  (application 0 (test-db-uri) :test)))
  (alter-var-root #'test-system component/start))

(defn stop-test-system []
    (alter-var-root #'test-system component/stop))
