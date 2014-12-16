(ns stack-spike.utility.debug
  "Debugging tools."
  (:require [clojure.tools.logging :refer [debug]]))

(def ^:dynamic *debug* true)

(defmacro dbg [& body]
  `(let [x# ~@body]
     (when *debug*
       (with-bindings {#'clojure.pprint/*print-miser-width* 120
                       #'clojure.pprint/*print-right-margin* 160}
         (debug (str "dbg: " (quote ~@body) " = " x#))))
     x#))
