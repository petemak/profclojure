(ns link-shortener.handler
  (:require [ring.util.request :as req]
            [ring.util.response :as res]
            [link-shortener.storage :as stg-api]))


;;
(defn retrieve-link
  "Reads the link associated with the provided id and redirects ot it. 
  Otherwise retunrs a 4040 response"
  [store id]
  (if-let [url (stg-api/get-link store id)]
    (res/redirect url)
    (res/not-found (str "Link for -[" id "]- was not found!"))))

;;
(defn register-link
  "Resnponds to a POST to register a link with the speified id"
  [store id {url :body}]
  (if (stg-api/create-link store id url)
    (res/response (str "/links/" id))
    (-> (format "The id provided -[%s]- is already in use!" id)
        (res/response)
        (res/status 422))))

;;
(defn update-link
  "Modify a link give an existing id"
  [store id new-url]
  (if (stg-api/update-link store id new-url)
    (res/response (str "/links/" id))
    (-> (format "The id provided -[%s]- is not registerred!" id)
        (res/response)
        (res/status 422))))


;; delet a link
(defn delete-link
  "Delet a link identified by the given id
  Ruturn the URL in the response"
  [store id]
  (if-let [url (stg-api/delete-link store id)]
    (res/response url)
    (-> (format "The id provided -[%s]- is not registerred" id)
        (res/response)
        (res/status 404))))
