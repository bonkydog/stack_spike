(ns stack-spike.interface.html.isomorphic
  (:require [clojure.java.io :as io]
            [hiccup.page :refer [html5 include-css include-js]])
  (:import [javax.script
            Invocable
            ScriptEngineManager]))

(def js (.getEngineByName (ScriptEngineManager.) "nashorn"))

(defn resource [path]
  (-> (str "public/js/" path)
      io/resource
      io/reader))

(defn render [edn]
  (try (.eval js "var global = this")
       (.eval js (resource "main-iso.js"))
       (let [view (.eval js "stack_spike.om_app")]
         (.invokeMethod
          ^Invocable js
          view
          "render_to_string"
          (-> edn
              list
              object-array)))
       (catch Exception e
         (prn e)
         (println (.getFileName e))
         (println (.getLineNumber e)))))


