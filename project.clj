(defproject stack_spike "0.1.0-SNAPSHOT"
  :description "A web application stack spike."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/clojurescript "0.0-2665"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [log4j "1.2.17"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.0.0"]
                 [org.om/om "0.8.0"]
                 [om-sync "0.1.1"]
                 [ring/ring "1.3.2"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [io.clojure/liberator-transit "0.3.0"]
                 [com.cognitect/transit-cljs "0.8.199"]
                 [bidi "1.14.0"]
                 [liberator "0.12.3-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [com.datomic/datomic-pro "0.9.5078"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j]]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [com.stuartsierra/component "0.2.2"]
                 [com.cemerick/url "0.1.1"]
                 [ring-mock "0.1.5"]
                 [clj-webdriver "0.6.1"]]
  :main stack-spike.core
  :Target-path "target/%s"

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all}
             :dev [:dev-env {:plugins [[com.cemerick/austin "0.1.6"]]
                             :source-paths ["dev"]
                             :dependencies [[weasel "0.4.2"]
                                            [org.clojure/tools.namespace "0.2.8"]
                                            [org.clojure/java.classpath "0.2.2"]
                                            [rmarianski/tidy-up "0.0.2"]]}]
             :test [:test-env]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/clj" "src/cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}
                        ;; :notify-command ["bash" "bin/notify-about-cljs-error.bash"]
                        }]}

  :repl-options {:init-ns user})
