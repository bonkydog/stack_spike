(ns stack-spike.core-test
  (:require [com.stuartsierra.component :as component]
            [clojure.test]
            [clj-webdriver.taxi :as taxi]
            [stack-spike.external.database :as db]
            [stack-spike.core :refer :all]
            [stack-spike.external.url :refer [local-host-name unused-port local-root-url]]
            [cemerick.url :refer [url]] ))

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
  (application (local-host-name) (unused-port) (test-db-uri)))

(def browser :firefox)

(def miliseconds-to-wait 1000)

(def ^:dynamic *test-om-interface* false)

(defn setup-browser-session! []
  (if-not (bound? #'taxi/*driver*)
    (taxi/set-driver! (taxi/new-driver {:browser browser})))
  (taxi/implicit-wait taxi/*driver* miliseconds-to-wait)
  taxi/*driver*)


(defonce ^:dynamic *test-root-url* "http://example.com")

(defn path->url [path]
  {:pre [path]}
  (str (url *test-root-url* path)))

(defn integration-test-fixture [t]
  (setup-browser-session!)
  (let [system (component/start (test-application))]
    (try
      (binding [*test-root-url* (local-root-url (:port (:app system)))]
        (t))
      (finally
        (db/destroy (:db system))
        (component/stop system)))))
