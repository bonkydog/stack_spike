(ns stack-spike.interface.resource.ship
  (:require [cognitect.transit :as transit]
            [ring.util.response :as ring]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [create-ship update-ship delete-ship]])
  (:import (java.io ByteArrayOutputStream)))

(defn transit-response [body]
  (let [stream (ByteArrayOutputStream. 4096)
        writer (transit/writer stream :json-verbose)]
    (transit/write writer body)
    (-> (ring/response (.toString stream))
        (ring/content-type "application/transit+json; charset=utf-8"))))

(defn ship-list [request]
  (transit-response (list-ships (entity-gateway (:db request)))))

(defn action [request]
  (let [[action arg] (:body request)
        eg (entity-gateway (:db request))
        result (case action
          :request-ship-create (create-ship eg arg)
          :request-ship-update (update-ship eg arg)
          :request-ship-delete (delete-ship eg (:db/id arg)))]
    (transit-response {:result result})))
