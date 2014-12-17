(ns stack-spike.interface.presenter.ship
  (:require [stack-spike.interface.html.ship :as html]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]))

(defn ship-path [ship]
  (r/path-for :ship :id (or (:db/id ship) "new")))

(defn ships-path []
  (r/path-for :ships))

(defn assoc-paths [ship]
  (assoc ship
         :path (ship-path ship)
         :collection-path (ships-path)))

(defn present-ship-index [ships]
  (let [view (map assoc-paths ships)]
    (html/index view)))

(defn present-ship-show [ship]
  (html/show (assoc-paths ship)))
