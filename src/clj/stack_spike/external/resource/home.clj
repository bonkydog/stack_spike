(ns stack-spike.external.resource.home
  (:require [liberator.core :refer [defresource]]))

(defresource home
  :available-media-types ["text/plain"]
  :handle-ok (fn [ctx]
               (str "Hello, world! Here is my database connection: "
                    (get-in ctx [:request :conn]))))
