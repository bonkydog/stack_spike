(ns stack-spike.external.browser
  (:require [clj-webdriver.taxi :refer :all]
            [stack-spike.utility.debug :refer [dbg]]
            [com.stuartsierra.component :as component]
            [stack-spike.external.web :as web]
            [clojure.string :as s]))

(defrecord Browser [browser web]
  component/Lifecycle
  (start [component]
    (let [browser (new-driver {:browser :firefox})]
      (set-driver! browser)
      (implicit-wait 3000)
      ;;(.addShutdownHook (Runtime/getRuntime) (Thread. #(quit browser)))
      (assoc component :web web, :browser browser)))
  (stop [component]
    (quit (:browser component))
    (dissoc component :browser :web)))

(defn new-browser []
  (map->Browser {}))

(defn visit [browser path]
  (to (:browser browser) (str (web/root-url (:web browser)) (s/replace-first path #"^/" ""))))
