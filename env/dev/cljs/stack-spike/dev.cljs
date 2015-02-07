(ns stack-spike.dev
  (:require [stack-spike.om-app :as om-app]
            [figwheel.client :as figwheel :include-macros true]
            [cljs.core.async :refer [put!]]
            [weasel.repl :as weasel]
            ;; [clojure.browser.repl :as repl]
            ))

(when (exists? js/document)
  (enable-console-print!)

  (figwheel/watch-and-reload
   :websocket-url "ws://localhost:3449/figwheel-ws"
   :jsload-callback (fn [] (om-app/main)))
  ;; (repl/connect "http://localhost:9000/repl")
  (weasel/connect "ws://localhost:9001" :verbose true)
  )
