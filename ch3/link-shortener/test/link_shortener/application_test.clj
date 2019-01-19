(ns link-shortener.application-test
  (:require [link-shortener.application :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]))

;; Fixture ensures the storage is cleared
;; before each test
(defn storage-fixture
  "Will clean storage before test runs"
  [f]
  (app-handler (mock/request :delete "/links"))
  (f))

(use-fixtures :each storage-fixture)

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
  (testing "Testing post route for registerringlinks"
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
  (testing "Testing get route for retrieving links"
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
  (testing "Testing put route for modifing links"
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




(deftest test-delete-link-route
  (testing "Testing delete route for unregistering links"
    (let [id "test-delete-001"         
          url "https://clojure.org"
          path (str "/links/" id)] 

      (testing "DELETE /links/:id"
        (testing "when the id exsists"
          (app-handler (mock/request :post path url))
          (let [response (app-handler (mock/request :delete path))]
            (testing "that the response code is 200"
              (is (= (:status response) 200))
              (is (= (:body response) url))
              (testing "and the link now returns a 404"
                (let [response (app-handler (mock/request :get path))]
                  (is (= (:status response) 404)))))))
        
        (testing "when the id does not exist"
          (let [response (app-handler (mock/request :delete "links/delete-bad-id"))]
            (testing "that the response status code is 404 unknown"
              (is (= (:status response) 404)))))))))



(deftest test-delete-links-route
  (testing "Testing delete route for unregistering links"
    (let [id-urls {"test-del-all-001" "https://clojure.org/1"
                   "test-del-all-002" "https://clojure.org/2"
                   "test-del-all-003" "https://clojure.org/3"}
          resp (doseq [[id url] id-urls]
                 (app-handler (mock/request :post (str "/links/" id) url)))] 

      (testing "DELETE /links"
        (let [response (app-handler (mock/request :delete "/links"))]
          (testing "after delete that the response code is 200"
            (is (= (:status response) 200))))
        
        (let [response (app-handler (mock/request :get (str "/links")))
              decoded-body (json/decode (:body response))]
          (testing "after deletion, rtrieving all links"
            (testing "that the response status code is 200 success"
              (is (= (:status response) 200)))
            (testing "that the results are empty"
              (is (empty? decoded-body)))))))))



(deftest test-get-all-links
  (testing "Testing get route for retrieving all routes"
    (let [id-urls {"test-get-all-001" "https://clojure.org/1"
                   "test-get-all-002" "https://clojure.org/2"
                   "test-get-all-003" "https://clojure.org/3"}
          resp (doseq [[id url] id-urls]
                 (app-handler (mock/request :post (str "/links/" id) url)))]

      
      (testing "GET /links"
        (testing "when the ids exists"
          (let [response (app-handler (mock/request :get "/links"))
                decoded-body (json/decode (:body response))]

            (testing "that the response status code is 200 success"
              (is (= (:status response) 200))
              (testing "and that the location contains the expected URL"
                (is (= (select-keys decoded-body ["test-get-all-001"
                                                  "test-get-all-002"
                                                  "test-get-all-003"]) id-urls))))))))))
