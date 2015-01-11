(ns user
    "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh-all refresh) :as tn]
            [environ.core :refer [env]]
            [stack-spike.core :refer :all]
            [clojure.test]
            [clojure.stacktrace :refer [print-stack-trace print-cause-trace]]
            [clojure.pprint :refer :all]
            [clojure.tools.logging :as log]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.use-case.entity-gateway :as eg]
            [stack-spike.external.url :refer [local-host-name]]
            [cemerick.austin.repls]
            [cemerick.austin]))


(defonce system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (application (local-host-name) (env :http-port) (env :datomic-uri) ))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))


(defn run-tests
  []
  (clojure.test/run-all-tests #"^stack-spike.*"))

(defn t []
  (refresh :after 'user/run-tests))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Miscellaneous tools

(defn show-methods [x] (sort
                        (filter
                         #(not (re-find #"^(__|const)" (str %)))
                                (map :name (:members (clojure.reflect/reflect x))))))

(defn eg []
  "Get an entity gateway for the current system."
  (entity-gateway (:db system)))

(def repl-env (reset! cemerick.austin.repls/browser-repl-env
                      (cemerick.austin/repl-env)))
(defn brepl []
  (cemerick.austin.repls/cljs-repl repl-env))
