(ns stack-spike.tools)

(defn log [& x]
  (js/console.log (clj->js x))
  x)
