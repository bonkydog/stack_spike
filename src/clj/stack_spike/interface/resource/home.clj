(ns stack-spike.interface.resource.home
  (:require [ring.util.response :as ring]
            [stack-spike.interface.html.home :as html]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.external.database :refer [entity-gateway]]))

(defn html-response [body]
  (-> (ring/response body)
      (ring/content-type "text/html")))

(defn om [request]
  (let [ships (list-ships (entity-gateway (:db request)))]
    (html-response (html/om ships))))

(defn home [_]
  (html-response (html/home)))
