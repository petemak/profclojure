(ns example-project.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.util.response :as ring-resp]            
            [ring.middleware.json :as ring-json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))


;; Echo handler
(defn echo
  "Echos back the body. If no body is specified 
  then the satus code 400 is returned"
  [body]
  (if (not-empty body)
    (-> (ring-resp/response body)
        (ring-resp/status 200)
        (ring-resp/content-type "text/plain")
        (ring-resp/charset "utf-8"))
    (-> (ring-resp/response "Invalid request: body not found!")
        (ring-resp/status 400))))


;; Echo handler
(defn echo-body
  "Echos back the contents of the request body. If no body is specified 
  then the satus code 400 is returned"
  [request]
  (echo (:body request)))




;; Middleware returns a functions that calls a handlr and cacthes
;; server errors and return a 500 error
 (defn wrap-http500-error
  "Returns a functions that calls the  handler and cacthes server-side 
  execution errors and returns an error message"
  [handler]
  (fn [request]
    (try (handler request) 
      (catch Exception e
        (-> (ring-resp/response (.getMessage e) )
            (ring-resp/status 500)
            (ring-resp/content-type "text/plain")
            (ring-resp/charset "utf-8"))))))


;;
;; PROBLEM: mutable object in request body 
;; 
;; {:protocol "HTTP/1.1",
;;  :remote-addr "localhost",
;;  :headers {"host" "localhost", "content-length" "17"},
;;  :server-port 80,
;;  :content-length 17,
;;  :uri "/",
;;  :server-name "localhost",
;;  :body
;;   #object[java.io.ByteArrayInputStream 0x5b77bc58
;;                              "java.io.ByteArrayInputStream@5b77bc58"],
;;  :scheme :http,
;;  :request-method :post}
;;
;; The proble with this request is that the body used a ByteArrayInputstream
;; Object. Once the content is read then the object is emptied.
;; So the content is lost. +++ MUTABLE ++++
;; user> (slurp (:body mr))
;; "This is the body!"
;; user> (slurp (:body mr))
;; ""
;; This middleware replaces the mutable ByteArrayInputStream with the
;; with the actual string value
(defn wrap-stream-body
  "This middleware replaces the mutable ByteArrayInputStream 
  with theactual string value"
  [handler]
  (fn [request]
    (if (instance? java.io.InputStream (:body request))
      (handler (update request :body slurp)) ;;call handler with body replaced
      (handler request))))


;;
(defn wrap-json-body
  "Handles parsing a JSON body. Decodes the body to a clojure map
  Note: expects a string"
  [handler]
  (fn [request]
    (if-let [updated-request (try
                               (update request :body json/decode)
                               (catch com.fasterxml.jackson.core.JsonParseException e nil))]
      (handler updated-request)
      (-> (ring-resp/response "Expected JSON in the request body")
          (ring-resp/status 400)))))



;; Middleware function for encooding the response to
;; JSON.
(defn wrap-json-response
  "Calls the specified handler and encodes the response body 
   to JSON and finally sets the content type"
  [handler]
  (fn [request]
    (-> (handler request)
        (update :body json/encode)
        (ring-resp/content-type "application/json"))))


;; NOTE: RE-EVENTS THE WHEEL - use ring-json/wrap-json-body instead
;; 
;; Handler for resolving system properitied
;; Does so by calling by wrapping the actual function wiht
;; the JSON response middware 
(def handle-get-info
  (;;wrap-json-response
   ring-json/wrap-json-response
   (fn [_]
     (-> {"java.version" (System/getProperty "java.version")
          "os.name" (System/getProperty "os.name")
          "os.version" (System/getProperty "os.version")}
          (ring-resp/response)))))
;;
;; 
(defn handle-clojurefy
  [request]
  (-> (:body request)
      (str)
      (ring-resp/response)
      (ring-resp/content-type "application/edn")))


;; NOTE: RE-EVENTS THE WHEEL - use ring-json/wrap-json-body instead
;; Routes that require body content
;; NOTE: uses custom middleware wrap-json-body
(def body-routes
  (-> (routes
       (ANY "/echo" [:as {body :body}] (echo body))
       ;;(POST "/clojurefy" [:as request] ((wrap-json-body handle-clojurefy) request))
       )
      (wrap-routes wrap-stream-body)))

;; Uses ring-json instead of custom middleware
(def json-routes
  (routes
   (POST "/clojurefy" [] (ring-json/wrap-json-body handle-clojurefy))))


;; routes that dont require body content
(defroutes non-body-routes
  (GET "/" [] "Ring Test Project")
  (GET "/film/:name" [name] (str "Film name: " name))
  (GET "/test500" [] (/ 1 0))
  (GET "/info" [] handle-get-info)
  (route/not-found "Not Found"))


(def app-routes
  (routes json-routes body-routes non-body-routes))


;; main hander handler
(def main-app
  (-> app-routes
      (wrap-http500-error)
      (wrap-defaults api-defaults)))


;;========================== TESTING PURPOSES =========================
;; This is only required to test if the input stream in body
;; is replaced correctly by wrap-stream-body middleware


;; test handler for echoing request body
(def echo-body-app
  (-> echo-body
      (wrap-http500-error)
      (wrap-stream-body)))
