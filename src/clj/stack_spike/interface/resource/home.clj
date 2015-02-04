(ns stack-spike.interface.resource.home
  (:require [ring.util.response :as ring]
            [stack-spike.interface.html.home :as html]))

(defn html-response [body]
  (-> (ring/response body)
      (ring/content-type "text/html")))

(defn om [_]
  (html-response (html/om)))

(defn home [_]
  (html-response (html/home)))
