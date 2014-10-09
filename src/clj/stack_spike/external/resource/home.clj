(ns stack-spike.external.resource.home
  (:require [liberator.core :refer [defresource]]))

(defresource home [db]
  :available-media-types ["text/plain"]
  :handle-ok (fn [ctx]
               (str "Hello, world! Here is my database uri: "
                    (:uri db))))
