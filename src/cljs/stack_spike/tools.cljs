(ns stack-spike.tools)

(defn log [& x]
  (if (exists? js/console)
    (js/console.log (clj->js x)))
  x)
