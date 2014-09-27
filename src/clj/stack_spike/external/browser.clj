(ns stack-spike.external.browser
  (:require [clj-webdriver.taxi :refer :all]
            [clojure.test :refer :all]
            [stack-spike.utility.debug :refer [dbg]]
            [com.stuartsierra.component :as component]
            [stack-spike.external.web :as web]
            [clojure.string :as s]))

(defrecord Browser [browser web]
  component/Lifecycle
  (start [component]
    (let [driver (new-driver {:browser :firefox})]
      (implicit-wait driver 3000)
      ;;(.addShutdownHook (Runtime/getRuntime) (Thread. #(quit driver)))
      (assoc component :web web, :driver driver)))
  (stop [component]
    (quit (:driver component))
    (dissoc component :driver :web)))

(defn new-browser []
  (map->Browser {}))

(defn visit [browser path]
  (to (:driver browser) (str (web/root-url (:web browser)) (s/replace-first path #"^/" ""))))

(defn assert-text-present [browser expected-content]
  (let [actual-content (text (find-element (:driver browser) {:css "body"}))]
    (is (<= 0
            (.indexOf
             actual-content
             expected-content))
        (str "expected to see \"" expected-content "\" in \"" actual-content "\""))))
