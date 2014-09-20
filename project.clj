(defproject stack_spike "0.1.0-SNAPSHOT"
  :description "A web application stack spike."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [environ "1.0.0"]
                 [om "0.7.1"]
                 [om-sync "0.1.1"]
                 [ring/ring "1.3.1"]
                 [ring-transit "0.1.2"]
                 [http-kit "2.1.16"]
                 [bidi "1.10.4"]
                 [liberator "0.12.1"]
                 [com.datomic/datomic-pro "0.9.4815"]
                 [com.stuartsierra/component "0.2.2"]
                 [ring-mock "0.1.5"]
                 [clj-webdriver "0.6.0"]]

  :main stack-spike.core
  :Target-path "target/%s"

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all}
             :dev [:dev-env {:plugins [[com.cemerick/austin "0.1.5"]]
                             :source-paths ["dev"]
                             :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                            [org.clojure/java.classpath "0.2.1"]]}]
             :test [:test-env]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/clj" "src/cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}]}
  :repl-options {:init-ns user})
