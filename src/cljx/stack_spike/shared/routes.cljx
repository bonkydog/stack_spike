(ns stack-spike.shared.routes
  (:require [cemerick.url :as u]
            [bidi.bidi :as b]
            #+clj [bidi.ring :as r]))

(def routes
  ["/" {:get {"" :home
              "ships" :ships
              ["ships/" :id] :ship
              "api/ships" :ships-api
              #+clj "js" #+clj (r/resources-maybe {:prefix "public/js/"})
              }
        :post {"api/action" :action}}])

(defn resolve [url-or-path]
  (let [path (:path (u/url url-or-path))]
    (b/match-route routes path :request-method :get)))

(defn path-for [route & params]
  (apply b/path-for routes route params))

(defn url-for [root-url route & params]
  (-> (u/url root-url)
    (assoc :path (apply path-for route params))
    str))
