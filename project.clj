(defproject stack_spike "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;; cljs paths needed here for browser repl analysis.
  ;; see https://github.com/tomjakubowski/weasel/issues/28
  :source-paths ["src/clj" "vendor/clj" "src/generated_clj"
                 "src/cljs" "vendor/cljs" "src/generated_cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-SNAPSHOT"] ; local build of master
                 ;; [org.clojure/clojurescript "0.0-2760"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [com.cemerick/piggieback "0.1.6-SNAPSHOT"]
                 ;; [weasel "0.5.0"] ; using local fork
                 [leiningen "2.5.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [log4j "1.2.17"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.0.0"]
                 [org.omcljs/om "0.8.8"]
                 [prismatic/om-tools "0.3.10"]
                 [ring/ring "1.3.2"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [com.fasterxml.jackson.datatype/jackson-datatype-json-org "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-core "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-databind "2.5.0"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.5.0"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [com.cognitect/transit-cljs "0.8.202"]
                 [ring-transit "0.1.3"]
                 [bidi "1.15.0" :exclusions [org.clojure/clojure com.cemerick/piggieback]]
                 [hiccup "1.0.5"]
                 [com.datomic/datomic-pro "0.9.5078"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j joda-time]]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [com.stuartsierra/component "0.2.2"]
                 [clj-webdriver "0.6.1"]
                 [cljs-http "0.1.25" :exclusions [com.cemerick/piggieback]]
                 [org.clojure/core.cache "0.6.4"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [com.keminglabs/cljx "0.5.0"]
            [lein-environ "1.0.0"]]

  :min-lein-version "2.5.0"

  :uberjar-name "stack_spike.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "vendor/cljs" "src/generated_cljs"]
                             :compiler {:main "stack-spike.dev"
                                        :output-to "resources/public/js/main.js"
                                        :output-dir "resources/public/js/out"
                                        :asset-path "/js/out"
                                        :optimizations :none
                                        :pretty-print true
                                        :source-map true}}
                       :iso {:source-paths ["src/cljs" "vendor/cljs" "src/generated_cljs"]
                             :compiler {:main "stack-spike.om-app"
                                        :output-to "resources/public/js/main-iso.js"
                                        :optimizations :advanced}}}}

  :cljx {:builds [{:source-paths ["src/cljx"]
                         :output-path "src/generated_clj"
                         :rules :clj}

                        {:source-paths ["src/cljx"]
                         :output-path "src/generated_cljs"
                         :rules :cljs}]}
  :clean-targets ^{:protect false} [:target-path "src/generated_clj" "src/generated_cljs"]
  :profiles {:dev {:repl-options {:init-ns dev
                                  ;; :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                                  }

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
                                             {:main "stack-spike.prod"
                                              :optimizations :advanced
                                              :pretty-print false}}}}}})
