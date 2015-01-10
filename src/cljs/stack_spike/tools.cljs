(ns stack-spike.tools)

(defn log [x]
  (.log js/console x)
  x)
