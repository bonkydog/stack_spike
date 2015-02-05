(ns stack-spike.interface.routes
  (:require [bidi.bidi :as b]
            [bidi.ring :as r]
            [cemerick.url :as u]))

(def routes
  ["/" {:get {"" :home
              "api/ships" :ships-api
              "js" (r/resources-maybe {:prefix "public/js/"})
              #".*" :om}
        :post {"api/action" :action}}])


(def client-side-routes
  ["/" {"ships" :ships
        ["ships/" :id] :ship}])

(defn path-for [route & params]
  (apply b/path-for routes route params))

(defn url-for [root-url route & params]
  (-> (u/url root-url)
    (assoc :path (apply path-for route params))
    str))

