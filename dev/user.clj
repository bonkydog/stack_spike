(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [datomic.api :as d :refer [db q]]
   ))

(def system nil)
(def conn nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
    (constantly :foo)))

(defn start
  "Starts the current development system."
  []
  )

(defn rebuild-database
  "Replace the database for the current development system."
  []
  )

(defn stop
  "Shuts down and destroys the current development system."
  []
  )

(defn go
  "Initializes the current development system and starts it running."
  []
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
