(ns stack-spike.core-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test]
            [stack-spike.external.database :as db]
            [stack-spike.core :refer :all]
            [stack-spike.external.url :refer [unused-port]]))

(defn test-db-uri []
  (str "datomic:mem://stack-spike-test-" (.getId (Thread/currentThread))))

(defn test-application []
  ;; There is a race condition here -- it's *possible* (though
  ;; unlikely) for this port to be claimed by someone else between here
  ;; and starting the web server.  We do it this way so that the port is
  ;; knowable and can be fed into the system map.
  ;;
  ;; If we tell Jetty to use port zero, it will pick a port
  ;; -- but then we have to ask it what it is, which introduces circular
  ;; dependencies between components. :-/ This is probably good enough
  ;; for now.)
  (application (unused-port) (test-db-uri)))

(defmacro defsystest
  "Define a test wrapped in a test system setup/teardown.  The system's
  Browser component will be supplied as an argument."
  [name [system-argument] & forms]
  `(clojure.test/deftest ~name []
     (let [system# (component/start (test-application))]
       (try
         (let [~system-argument system#]
           ~@forms)
         (finally
           (db/destroy (:db system#))
           (component/stop system#))))))
