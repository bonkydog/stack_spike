(ns stack-spike.interface.routes
  (:require [bidi.bidi :as b]
            [bidi.ring :as r]
            [bidi.server :as s]
            [cemerick.url :as u]))

(import java.net.URL)

(def routes
  ["/" {"" :home
        "ships" :ships
        ["ships/" :id] :ship
        "js" (s/resources-maybe {:prefix "public/js/"})}])

(defn path-for [route & params]
  (apply b/path-for routes route params))

(defn url-for [root-url route & params]
  (-> (u/url root-url)
    (assoc :path (apply path-for route params))
    str))
