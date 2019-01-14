(ns link-shortener.application-test
  (:require [link-shortener.application :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))


(deftest test-app
  (testing "application error routes"
    (testing "invalid get no request path"
      (let [response (app-handler (mock/request :get "/"))]
        (testing "that status is 404"
          (is (= (:status response) 404)))))
    (testing "invalid get with unknown request path"
      (let [response (app-handler (mock/request :get "/invalid-route"))]
        (testing "that status code is a 404"
          (is (= (:status response) 404)))
        (testing "that error message is as epected"
          (is (= (:body response) "Not Found")))))))


;;
(deftest test-post-post
  (testing "Testing actual application routes"
    (let [id "test-put-001"
          url "https://clojure.org"
          path (str "/links/" id)
          response (app-handler (mock/request :post  path url))]
      (testing "POST /links/:id"       
        (testing "that response statuse code is 200 OK"
          (is (= (:status response) 200))
          (testing "and the body contains the path"
            (is (= (:body response) path))))))))




(deftest test-get-route
  (testing "Testing actual application routes"
    (let [id "test-get-001"
          url "https://clojure.org"
          path (str "/links/" id)] 

      (testing "GET /links/:id"
        (testing "when the id exists"
          (app-handler (mock/request :post  path url))
          (let [response (app-handler (mock/request :get path))]
            (testing "that the response status code is 302 redirect"
              (is (= (:status response) 302))
              (testing "and that the location contains the expected URL"
                (is (= (get-in response [:headers "Location"]) url))))))

        (testing "when a link does not exits"
          (let [response (app-handler (mock/request :get "/links/get-bad-id"))]
            (testing "that responce is a 404 not found!"
              (is (= (:status response) 404)))))))))



(deftest test-put-route
  (testing "Testing actual application routes"
    (let [id "test-put-001"         
          url "https://clojure.org"
          new-url "https://clojurescript.org"
          path (str "/links/" id)] 

      (testing "PUT /links/:id"
        (testing "when the id exsists"
          (app-handler (mock/request :post  path url))
          (let [response (app-handler (mock/request :put path new-url))]
            (testing "that the responce code is 200"
              (is (= (:status response) 200)))))
        
        (testing "when the id does not exist"
          (let [response (app-handler (mock/request :put "links/put-bad-id" new-url))]
            (testing "that the response status code is 404 unknown"
              (is (= (:status response) 404)))))))))




(deftest test-delete-route
  (testing "Testing actual application routes"
    (let [id "test-delete-001"         
          url "https://clojure.org"
          path (str "/links/" id)] 

      (testing "DELETE /links/:id"
        (testing "when the id exsists"
          (app-handler (mock/request :post  path url))
          (let [response (app-handler (mock/request :delete path))]
            (testing "that the responce code is 204"
              (is (= (:status response) 204))
              (testing "and the link now returns a 404"
                (let [response (app-handler (mock/request :get path))]
                  (is (= (:status response) 404)))))))
        
        (testing "when the id does not exist"
          (let [response (app-handler (mock/request :delete "links/delete-bad-id"))]
            (testing "that the response status code is 404 unknown"
              (is (= (:status response) 404)))))))))

