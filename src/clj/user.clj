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
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            [cljs.repl.browser]
            [leiningen.core.main :as lein]))

(def is-dev? (env :is-dev))

(defn weasel-repl []
  (piggieback/cljs-repl :repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)))

(defn piggieback-repl []
  (cemerick.piggieback/cljs-repl :repl-env (cljs.repl.browser/repl-env :port 9000)) )

(defn start-figwheel []
  (future
    (print "Starting figwheel.\n")
    (lein/-main "figwheel" :app :iso)))

(defonce system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (application (local-host-name) 8080 "datomic:dev://localhost:4334/stack-spike-dev" ))))

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
