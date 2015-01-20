(ns stack-spike.interface.resource.home
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.html.home :as html]))

(defresource om [_ _]
  :available-media-types ["text/html"]
  :handle-ok html/om)

(defresource home [_ _]
  :available-media-types ["text/html"]
  :handle-ok html/home)
