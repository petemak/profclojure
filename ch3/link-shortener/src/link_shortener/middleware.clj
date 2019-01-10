(ns link-shortener.middleware
  (:import java.io.InputStream))

;;
;; Middleware slurps the request body
(defn wrap-slurp-body
  "The request body is provided as a java InputStream, this middleware will
  retunr a handler function that slurps the body and replaces it with a string
  before calling the handler"
  [handler]
  (fn [request]
    (if (instance? InputStream (:body request))
      (let [slurped-request (update request :body slurp)]
        (handler slurped-request))
      (handler request))))
