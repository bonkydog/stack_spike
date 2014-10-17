(ns stack-spike.interface.presenter.ship
  (:require [stack-spike.interface.html.ship :as html]))

(defn present-ship-index [ships]
  (html/index ships))

(defn present-ship-show [ship]
  (html/show ship))

