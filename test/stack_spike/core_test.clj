(ns stack-spike.core-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test]
            [stack-spike.core :refer :all]))

(defn test-db-uri []
  (str "datomic:mem://stack-spike-test-" (.getId (Thread/currentThread))))

(defn test-application []
  (application 0, (test-db-uri)))

(defmacro defsystest
  "Define a test wrapped in a test system setup/teardown.  The system's
  Browser component will be supplied as an argument."
  [name [system-argument] & forms]
  `(clojure.test/deftest ~name []
     (let [system# (component/start (test-application))]
       (try
         (let [~system-argument system#]
           ~@forms)
         (finally (component/stop system#))))))
