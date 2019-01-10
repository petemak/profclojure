(ns link-shortener.routes
  "Exposes "
  (:require [link-shortener.handler :as handler]
            [link-shortener.middleware :as middleware]
            [compojure.route :as route]
            [compojure.core :refer :all]))


;; Defines all endpoints that the services will provide
(defroutes app-routes
  (GET "/" [] "Link Shortner - v0.0.1")
  (route/not-found "Invalid request!"))



;;
(defn shortener-routes
  [stg]
  (-> (routes
       (POST "/links/:id" [id :as request] (handler/register-link stg id request))
       (route/not-found "Not Found"))
      (wrap-routes middleware/wrap-slurp-body)))
