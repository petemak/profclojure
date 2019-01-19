(ns link-shortener.routes
  "Exposes "
  (:require [link-shortener.handler :as handler]
            [link-shortener.middleware :as middleware]
            [compojure.route :as route]
            [compojure.core :refer :all]))


;;
(defn shortener-routes
  [strg]
  (-> (routes
       (GET    "/links/:id" [id] (handler/retrieve-link strg id))
       (POST   "/links/:id" [id :as request] (handler/register-link strg id request))
       (PUT    "/links/:id" [id :as request] (handler/update-link strg id request))
       (DELETE "/links/:id" [id] (handler/delete-link strg id))
       (DELETE  "/links"    [] (handler/delete-links strg))
       (GET     "/links"     [] (handler/retrieve-all-links strg))
       (route/not-found "Not Found"))
      (wrap-routes middleware/wrap-slurp-body)))
