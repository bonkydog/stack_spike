(ns stack-spike.interface.presenter.ship
  (:require [stack-spike.interface.html.ship :as html]
            [stack-spike.interface.routes :as r]
            [bidi.bidi :as b]
            [stack-spike.utility.debug :refer [dbg]]))

(defn ship-path [ship]
  (b/path-for r/routes :ship :id (:db/id ship)))

(defn present-ship-index [ships]
  (let [view (map (fn [ship] (assoc ship :path (ship-path ship))) ships)]
    (html/index view)))

(defn present-ship-show [ship]
  (html/show ship))
