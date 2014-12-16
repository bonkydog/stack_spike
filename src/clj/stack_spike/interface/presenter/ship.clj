(ns stack-spike.interface.presenter.ship
  (:require [stack-spike.interface.html.ship :as html]
            [stack-spike.interface.routes :as r]
            [bidi.bidi :as b]
            [stack-spike.utility.debug :refer [dbg]]))

(defn ship-path [ship]
  (b/path-for r/routes :ship :id (or (:db/id ship) "new")))

(defn assoc-ship-path [ship]
  (assoc ship :path (ship-path ship)))

(defn present-ship-index [ships]
  (let [view (map assoc-ship-path ships)]
    (html/index view)))

(defn present-ship-show [ship]
  (html/show (assoc-ship-path ship)))
