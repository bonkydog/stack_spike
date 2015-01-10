(ns stack-spike.interface.resource.home
  (:require [liberator.core :refer [defresource]]
            [stack-spike.interface.html.home :as html]))

(defresource home [db _]
  :available-media-types ["text/html"]
  :handle-ok html/home)
