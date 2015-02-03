(ns stack-spike.routes
  (:require [bidi.bidi :as bidi])
  (:import [goog Uri]))

(def routes
  ["/om" {"/ships" :ships
          ["/ships/" :id] :ship}])

(defn current-url []
  (.-href (.-location js/document)))

(defn resolve [url-or-path]
  (let [path (.getPath (Uri. url-or-path))]
    (println path)
    (bidi/match-route routes path)))
