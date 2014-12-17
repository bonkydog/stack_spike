(ns stack-spike.interface.routes
  (:require [bidi.bidi :as b]
            [cemerick.url :as u]))

(import java.net.URL)

(def routes
  ["/" {"" :home
        "ships" :ships
        ["ships/" :id] :ship}])

(defn path-for [route & params]
  (apply b/path-for routes route params))

(defn url-for [root-url route & params]
  (-> (u/url root-url)
    (assoc :path (apply path-for route params))
    str))
