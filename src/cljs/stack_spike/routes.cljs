(ns stack-spike.routes
  (:require [bidi.bidi :as bidi])
  (:import [goog Uri]))

(def routes
  ["/" {"ships" :ships
        ["ships/" :id] :ship}])

(defn current-url []
  (if (exists? js/document)
    (.-href (.-location js/document))
    "/ships#nashorn"))

(defn resolve [url-or-path]
  (let [path (.getPath (Uri. url-or-path))]
    (println path)
    (bidi/match-route routes path)))
