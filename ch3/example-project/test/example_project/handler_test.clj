(ns example-project.handler-test
  (:require [clojure.test :refer :all]
            [clojure.string :as st]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [example-project.handler :refer :all]))

(deftest test-app-routes
  (testing "Main route"
    (let [response (main-app (mock/request :get "/"))]
      (testing "that status is 200"
        (is (= (:status response) 200)))
      (testing "that the message in the body is as exected "
        (is (= (:body response) "Ring Test Project")))))

  (testing "Not-found route"
    (let [response (main-app (mock/request :get "/invalid"))]
      (testing "that the status code for an invalid request is 404" 
        (is (= (:status response) 404)))
      (testing "that the body of an invalid request contains the expected message"
        (is (= (:body response) "Not Found")))))

  (testing "Film route"
    (let [response (main-app (mock/request :get "/film/Avatar"))]
      (testing "that the status code is 200"  
        (is (= (:status response) 200)))
      (testing "that the body contains the expected message for film Avatar" 
        (is (= (:body response) (str "Film name: Avatar")))))))

(deftest test-http500-middleware
  (testing "HTTP 500 middleware"
    (let [response (main-app (mock/request :get "/test500"))]
      (testing "that the status code must be 500"
        (is (= (:status response) 500)))
      (testing "The body must contain the exception message for devision by zero" 
        (is (= (:body response) "Divide by zero" ))))))

;;
(deftest test-wrap-stream-body
  (testing "wrap-steam-body middleware"
    (testing "and a body exists" 
      (let [response (echo-body-app (mock/request :post "/" "Wrap stream test"))]
        (testing "then stattus code must be 200"
          (is (= (:status response) 200)))
        (testing "secondly the body must not be mutable java.io.InputStream object"
          (is (not (instance? java.io.InputStream (:body response)))))
        (testing "and finally the mutable stream must be replaced by the slurped content"
          (is (= (:body response "Wrap stream test"))))))
    (testing "or error case - when a body is missing"
      (let [response (echo-body-app (mock/request :get "/"))]
        (testing "then the status code must be 400 for client error"
          (is (= (:status response) 400))))) ))


(deftest test-echo-body-handler
  (testing "echo-body handler"
    (testing "invalid request - no body"
      (let [response (main-app (mock/request :post "/echo"))]
        (testing "so the status code must be 400"
          (= (:status response) 400))))
    (testing "and a valid request with a body"
      (let [response (main-app (mock/request :post "/echo" "--- ECHO ---"))]
        (testing "that status code is 200"
          (is (= (:status response) 200)))
        (testing "that body content should contain the expected message"
          (is (re-find #"ECHO" (:body response))))
        (testing "that body content is as expected"
          (is (= (:body response) "--- ECHO ---")))))))



(deftest test-clojurefy
  (testing "the /clojurefy route endpoint"
    (testing "when valid JSON is provided"
      (let [clojure-map {:user "uabx" :name "Vivs"}
            json-req (json/encode clojure-map)
            response (main-app (-> (mock/request :post "/clojurefy" json-req)
                                   (mock/content-type "application/json")))]
        (testing "that the status code must be 200"
          (= (:status response) 200))
        (testing "that the Clojure map returned is the clojure-map"
          (= (:body response) (str clojure-map)))))
    (testing "when provided and invalid JSON"
      (let [response (main-app (-> (mock/request :post "/clojurefy" "*# INVALID %!")
                                   (mock/content-type "application/json")))]
        (testing "that status code is 400"
          (is (= (:status response) 400)))
        (testing "that the response body contains a message"
          (is (not-empty (:body response))))))))



(deftest test-get-imfo
  (testing "the /info route"
    (testing "when request is valid"
      (let [response (main-app (mock/request :get "/info"))]
        (testing "that status is 200"
          (clojure.pprint/pprint (:body response))
          (is (not-empty (:body response))))
        (testing "that the content-type is JSON"
          (is (st/includes? (get-in response [:headers "Content-Type"]) "application/json")))        
        (testing "that response body contains valid json"
          (let [info-map (json/decode (:body response))]
            (testing "that the decoded map exisis"
              (is (not= nil info-map)))
            (testing "that the decoded map contains expected keys"
              (is (= (set (keys info-map)) #{"java.version" "os.name" "os.version"})) ))) ))
    (testing "when request is valid but body has some content"
      (let [response (main-app (mock/request :get "/info" "#$%@*?/,.%&!"))]
        (testing "that status is 200"
          (is (not-empty (:body response))))
        (testing "that the content-type in response is JSON"
          (is (st/includes? (get-in response [:headers "Content-Type"]) "application/json")))
        (testing "that response body contains valid json"
          (let [info-map (json/decode (:body response))]
            (testing "that the decoded map exisis"
              (is (not= nil info-map)))
            (testing "that the decoded map contains expected keys"
              (is (= (set (keys info-map)) #{"java.version" "os.name" "os.version"})) )))))))
