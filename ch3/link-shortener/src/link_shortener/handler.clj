(ns link-shortener.handler
  (:require [ring.util.request :as req]
            [ring.util.response :as res]
            [ring.middleware.json :refer [wrap-json-response]]
            [link-shortener.storage :as strg-api]))



;;
(defn register-link
  "Resnponds to a POST to register a link with the speified id"
  [strg id {url :body}]
  (if (strg-api/create-link strg id url)
    (res/response (str "/links/" id))
    (-> (format "The id provided -[%s]- is already in use!" id)
        (res/response)
        (res/status 422))))

;;
(defn update-link
  "Modify a link give an existing id"
  [strg id new-url]
  (if (strg-api/update-link strg id new-url)
    (res/response (str "/links/" id))
    (-> (format "The id provided -[%s]- is not registerred!" id)
        (res/response)
        (res/status 422))))


;; delete a link
(defn delete-link
  "Delet a link identified by the given id
  Ruturn the URL in the response"
  [strg id]
  (if-let [url (strg-api/delete-link strg id)]
    (res/response url)
    (-> (format "The id provided -[%s]- is not registerred" id)
        (res/response)
        (res/status 404))))

;; This deletes all entries
(defn delete-links
  "Clear storage of all links"
  [strg]
  (strg-api/delete-links strg)
  (res/response ""))


;;
(defn retrieve-link
  "Reads the link associated with the provided id and redirects ot it. 
  Otherwise retunrs a 404 response"
  [strg id]
  (if-let [url (strg-api/get-link strg id)]
    (res/redirect url)
    (res/not-found (str "Link for -[" id "]- was not found!"))))


;;
;; retrieve all links
(defn retrieve-all-links
 "Return a list of all registerred links"
 [strg]
 (wrap-json-response
  (fn [req]
    (res/response (strg-api/get-links strg)))))


