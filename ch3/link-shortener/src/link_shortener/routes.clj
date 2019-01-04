(ns link-shortener.routes
  "Exposes "
  (:require [link-shortener.handler :as hdlr]
            [link-shortener.middleware :as mdwr]
            [compojure.route :as route]
            [compojure.core :refer :all]))


;; Defines all endpoints that the services will provide
(defroutes app-routes
  (GET "/" [] "Link Shortner - v0.0.1")
  (route/not-found "Invalid request!"))
