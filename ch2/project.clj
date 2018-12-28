(defproject ch2 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler ch2.handler/app
         :nrepl {:start? true
                 :port 60000}}
  :main ch2.core
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [ring/ring-mock "0.3.2"]
                                  [clj-http "3.9.1"]]}})
