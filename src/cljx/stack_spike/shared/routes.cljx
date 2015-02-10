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

(defn resolve-url [url-or-path]
  (let [path (:path (u/url url-or-path))]
    (b/match-route routes path :request-method :get)))

(defn path-for
  ([route] (path-for route {}))
  ([route params]
   (b/unmatch-pair routes {:handler route :params (merge {:request-method :get} params)})))

(defn url-for
  ([root-url route] (url-for route {}))
  ([root-url route params]
   (-> (u/url root-url)
       (assoc :path (apply path-for route params))
       str)))
