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

(defn- render-fn* []
  (try (.eval js "var global = this")
       (.eval js (resource "main-iso.js"))
       (let [view (.eval js "stack_spike.om_app")]
         (fn [edn]
           (.invokeMethod
            ^Invocable js
            view
            "render_to_string"
            (-> edn
                list
                object-array))))
       (catch Exception e
         (prn e)
         (println (.getFileName e))
         (println (.getLineNumber e)))))


(defn render-fn
  []
  (let [pool (ref (repeatedly 3 render-fn*))]
    (fn render [state-edn]
      (let [rendr (dosync
                   (let [f (first @pool)]
                     (alter pool rest)
                     f))
            rendr (or rendr (render-fn*))
            html (rendr state-edn)]
        (dosync (alter pool conj rendr))
        html))))
