(defproject stack_spike "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2760"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [com.cemerick/piggieback "0.1.5"]
                 [weasel "0.5.0"]
                 [leiningen "2.5.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [log4j "1.2.17"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.0.0"]
                 [org.om/om "0.8.1"]
                 [prismatic/om-tools "0.3.10"]
                 [om-sync "0.1.1"]
                 [ring/ring "1.3.2"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [com.fasterxml.jackson.datatype/jackson-datatype-json-org "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-core "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-databind "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.5.0"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [com.cognitect/transit-cljs "0.8.202"]
                 [ring-transit "0.1.3"]
                 [bidi "1.15.0" :exclusions [org.clojure/clojure]]
                 [hiccup "1.0.5"]
                 [com.datomic/datomic-pro "0.9.5078"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j joda-time]]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [com.stuartsierra/component "0.2.2"]
                 [clj-webdriver "0.6.1"]
                 [cljs-http "0.1.25"]
                 [org.clojure/core.cache "0.6.4"]]


  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-environ "1.0.0"]]

  :min-lein-version "2.5.0"

  :uberjar-name "stack_spike.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :source-map    "resources/public/js/out.js.map"
                                        :preamble      ["react/react.min.js"]
                                        :externs       ["react/externs/react.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns user
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :plugins [[lein-figwheel "0.1.4-SNAPSHOT"]]

                   :figwheel {:http-server-root "public"
                              :port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:is-dev true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}}

             :uberjar {:hooks [leiningen.cljsbuild]
                       :env {:production true}
                       :omit-source true
                       :aot :all
                       :cljsbuild {:builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
