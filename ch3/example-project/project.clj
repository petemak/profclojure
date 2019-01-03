(defproject example-project "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.8.1"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler example-project.handler/main-app}
  :profiles
  {:dev {:dependencies [[clj-http "3.9.1"]
                        [javax.servlet/servlet-api "2.5"]
                        [org.clojure/tools.namespace "0.2.11"]
                        [ring/ring-mock "0.3.2"]]}})
