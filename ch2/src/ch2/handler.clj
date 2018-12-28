(ns ch2.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ch2.core :as tasks]))


;; Stores applications state
(def id-atom (atom 0))

;; generate next identifier
(defn next-id
  "Generate next unique identifier"
  []
  (swap! id-atom inc))

;; Compojure routes
;;
;; Testing routes
;; GET:  (:body (c/get "http://localhost:3000/api/tasks"
;;                     {:as :json}))
;; POST: (:body (c/post "http://localhost:3000/api/tasks"
;;                      {:form-params {:category "Work"
;;                                     :title "Datomic"
;;                                     :details "Learn Datomic querry language"}
;;                       :as :json}))
;; DELETE: (:body (c/delete "http://localhost:3000/api/tasks/3"
;;                          {:as :json}))
(defroutes app-routes
  (GET "/api/tasks/:category" [category]
       {:body (tasks/get-tasks category)})
  (POST "/api/tasks" [category title details]
        {:body (tasks/add-task category title details)})
  (DELETE "/api/tasks/:catgory" [category]
          {:body (tasks/remove-category category)})
  (DELETE "/api/tasks/:category/:task-id" [category task-id]
          {:body (tasks/remove-task category (Integer/parseInt task-id))})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-json-response)))
