(ns stack-spike.interface.resource.home
  (:require [ring.util.response]
            [stack-spike.interface.html.home :as html]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.external.database :refer [entity-gateway]]))

(defn html-response [body]
  (-> (ring.util.response/response body)
      (ring.util.response/content-type "text/html")))

(defn om [request]
  (let [ships (list-ships (entity-gateway (:db request)))]
    (html-response (html/om (ring.util.request/request-url request) ships))))

(defn home [_]
  (html-response (html/home)))
