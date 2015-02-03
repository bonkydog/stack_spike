(ns stack-spike.interface.resource.home
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.html.home :as html]))

(defresource om []
  :available-media-types ["text/html"]
  :handle-ok html/om)

(defresource home []
  :available-media-types ["text/html"]
  :handle-ok html/home)
