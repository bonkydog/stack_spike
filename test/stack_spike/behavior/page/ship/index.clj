(ns stack-spike.behavior.page.ship.index
  (:require [clojure.test :refer :all]
            [stack-spike.behavior.browser :as browser]
            [stack-spike.core-test :refer [*test-om-interface*]]
            [clj-webdriver.taxi :refer :all]))

(defn path []
  (str (if *test-om-interface* "#" "" ) "/ships"))

(defn visit []
  (browser/visit (path)))

(defn arrive []
  (browser/arrive (path)))

(defn id-of-first-ship []
  (Long/parseLong (text (element ".ship .id"))))

(defn new-ship []
  (click "a.new-ship"))

(defn edit-ship [ship-id]
  (click (str "#ship-" ship-id " a.edit")))

(defn assert-no-ships-listed []
  (is (not (exists? "td.ship"))))

(defn assert-test-ship-listed []
  (is (find-element {:css "tr.ship .name" :text "Test Ship"})))

(defn assert-exactly-one-ship-listed []
  (is (= 1 (count (find-elements {:css "tr.ship"})))))

(defn assert-test-ship-changed []
  (is (find-element {:css "tr.ship .name" :text "Different Name"})))

(defn delete-ship [ship-id]
  (click (str "#ship-" ship-id " a.delete")))
