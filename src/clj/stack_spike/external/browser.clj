(ns stack-spike.external.browser
  (:require [com.stuartsierra.component :as component]
            [clj-webdriver.taxi :refer :all]))

(defrecord Browser [browser]
  component/Lifecycle
  (start [component]
    (let [browser (new-driver {:browser :firefox})]

      (set-driver! browser)
      (implicit-wait 3000)
      ;;(.addShutdownHook (Runtime/getRuntime) (Thread. #(quit browser)))
      (assoc component :browser browser)))
  (stop [component]
    (quit (:browser component))
    (dissoc component :browser)))

(defn new-browser []
  (map->Browser {}))
