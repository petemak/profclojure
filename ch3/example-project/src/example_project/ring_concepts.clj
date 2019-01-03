(ns example-project.ring-concepts
  (:require [ring.mock.request :as mock]
            [ring.util.response :as ring-util]
            [example-project.handler :as handler]))

;; Creaating a request using ring.mock.request

;; 
;; {:protocol "HTTP/1.1",
;;  :server-port 80,
;;  :server-name "localhost",
;;  :remote-addr "localhost",
;;  :uri "/film/Avatar",
;;  :scheme :http,
;;  :request-method :get,
;;  :headers {"host" "localhost"}}
(def req (mock/request :get "/film/Avatar"))


;; Create response using the hadler function
;;
;; {:status 200,
;;  :headers
;;  {"Content-Type" "text/html; charset=utf-8",
;;   "Set-Cookie"
;;   ("ring-session=fbfdbd13-f48b-4e49-8c26-f2cb21e44b72;Path=/;HttpOnly;SameSite=Strict"),
;;   "X-XSS-Protection" "1; mode=block",
;;   "X-Frame-Options" "SAMEORIGIN",
;;   "X-Content-Type-Options" "nosniff"},
;;  :body "Film name: Avata"}
(def resp0 (handler/main-app (mock/request :get "/film/Avata")))

(clojure.pprint/pprint resp0)


;;
;; Creating a response with ring.util.response
;;
;;
;; {:status 404,
;;  :headers {"Content-Type" "text/html; charset=utf-8"},
;;  :body "Not found"}
(def resp1 (-> (ring-util/response "Not found")
               (ring-util/status 404)
               (ring-util/content-type "text/html")
               (ring-util/charset "utf-8")))

(clojure.pprint/pprint resp1)


;; ================ Handler
;; handlers take one argument, a map representing a HTTP request,
;; and return a map representing the HTTP response.
;;
;; This example handler returns the clients address in the :body
;; element of the response map
(defn whats-my-ip
  "Return the clients address provided in the field
   remode-addr of the the request"
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (:remote-addr request)})



;;================== Middleware
;; Middleware are higher-level functions that add additional functionality
;; to handlers. The first argument of a middleware function should be a handler,
;; and its return value should be a new handler function that will call the
;; original handler.

;; Wrap to add content type
(defn wrap-content-type
  "Returns a function that will call the roginalspecified  handler
   and enrich the response header with content type specified"
  [handler content-type]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Content-Type"] content-type))))


(def app  (wrap-content-type whats-my-ip "text/plain"))

(clojure.pprint/pprint (app req))


;; Middleware returns a functions that calls a handlr and cacthes
;; server errors and return a 500 error
 (defn wrap-http500-error
  "Returns a functions that calls the  handler and cacthes server-side 
  execution errors and returns an error message"
  [handler]
  (fn [request]
    (try (handler request) 
      (catch Exception e
        (-> (ring-util/response (str "Server error: " (.getMessage e)))
            (ring-util/status 500)
            (ring-util/content-type "text/plain")
            (ring-util/charset "utf-8"))))))



;;
(def http500handler (wrap-http500-error handler/main-app))
(def http500resp (http500handler (mock/request :get "/trouble")))
(clojure.pprint/pprint http500resp)




(def echoresp (handler/main-app (-> (mock/request :post "/echo")
                                    (mock/body "--- MOCK ---"))))
(clojure.pprint/pprint echoresp)
