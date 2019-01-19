(ns link-shortener.config
  (:require [clojure.edn :as edn]
            [environ.core :refer [env]]))


(defn from-env
  []
  {:api-name (or (env :api-name) "Link Shotener")
   :api-version (or (env :api-version) "0.0.1")})


(defn from-file
  []
  (edn/read-string (try
                     (slurp "dev.edn")
                     (catch Throwable e "{}" ))))


(defn config
  []
  (merge (from-env) (from-file)))
