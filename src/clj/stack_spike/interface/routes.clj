(ns stack-spike.interface.routes
  (:require [bidi.bidi :as b]
            [bidi.ring :as r]
            [cemerick.url :as u]))

(import java.net.URL)

(def routes
  ["/" [["" :home]
        ["api/ships" :ships]
        ["api/action" :action]
        ["js" (r/resources-maybe {:prefix "public/js/"})]
        [[#".*"] :om]]])

(defn path-for [route & params]
  (apply b/path-for routes route params))

(defn url-for [root-url route & params]
  (-> (u/url root-url)
    (assoc :path (apply path-for route params))
    str))
()
