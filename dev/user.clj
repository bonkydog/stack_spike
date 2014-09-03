(ns user
    "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]
            [stack-spike.core :refer :all]
            [clojure.test]))

(def system nil)

(defn init []
  (alter-var-root #'system
    (constantly (stack-spike-system default-config))))

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

(defn show-methods [x] (sort (filter #(not (re-find #"^(__|const)" (str %))) (map :name (:members (clojure.reflect/reflect x))))))
