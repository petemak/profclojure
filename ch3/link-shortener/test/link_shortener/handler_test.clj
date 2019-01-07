(ns link-shortener.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [link-shortener.handler :refer :all]
            [link-shortener.storage :as stg-api]
            [link-shortener.storage.in-memory :as stg-impl]
            [ring.mock.request :as mock]))

(deftest retrieve-link-test
  (testing "The link retrieval handler"
    
    (let [id "testid"
          url "https://clojure.org"
          strg (stg-impl/get-mem-storage)]
      
      ;;Pre-load link with protocol
      (stg-api/create-link strg id url)

      (testing "when the link exists in the store"
        (let [response (retrieve-link strg id)]
          (testing "then the response must be a 302 for redirect"
            (is (= (:status response) 302))
            (testing "and the URL must be in the location header"
              (is (= (get-in response [:headers "Location"]) url))))))
      
      (testing "when the link is not stored"
        (let [response (retrieve-link strg "wrongid")]  
          (testing "then the response must be a 404 for unknown "
            (is (= (:status response) 404))))))))


(deftest register-link-test
  (testing "The register link hander"
    (let [id  "testid"
          url "https://clojure.org"
          request (-> (mock/request :post "/links" url)
                      (update :body slurp))
          strg (stg-impl/get-mem-storage)]
      (testing "when id is valid and not in use"
        (stg-api/reset strg)
        (let [response (register-link strg id request)]
          (testing "then the response status must be 200 OK"
            (is (= (:status response) 200))
            (testing "and the body must contain the request string"
              (is (= (:body response) "/links/testid"))))))

      (testing "when id in use"
        (let [response (register-link strg id request)]
          (testing "then th response must be 422"
            (is (= (:status response) 422))))))))


(deftest update-link-test
  (testing "The update link handler"
    (let [id "testid"
          url "https://clojure.org"
          new-url "https://clojure.org"
          strg (stg-impl/get-mem-storage)]
      
      ;;Pre-load link with protocol
      (stg-api/reset strg)
      (stg-api/create-link strg id url)

      (testing "when the id exists"
        (let [response (update-link strg id new-url)]
          (testing "then the response status must be 200 OK"
            (is (= (:status response) 200))
            (testing "and the body must contain the request string"
              (is (= (:body response) "/links/testid")))
      
            (testing "and link muts be changed when read back"
              (let [response (retrieve-link strg id)]
                (is (= (:status response) 302))
                (is (= (get-in response [:headers "Location"]) new-url)))))))

      (testing "when the id doesn't exist"
        (let [response (update-link strg "invalid-id" "http://some-link.com/test")]
          (testing "that the status is 422"
            (is (= (:status response) 422))))))))

(deftest delete-link-test
  (testing  "Tests fullfilment of storage contract for deletion"
    (let [id  "test-id"
          url "https://test.domain.com/clojure"
          strg (stg-impl/get-mem-storage)]

      ;;Pre-load link with protocol
      (stg-api/reset strg)
      (stg-api/create-link strg id url)

      (testing "when and exisiting link is deleted"
        (let [response (delete-link strg id)] 
           (testing "then the response must be 200 OK"
             (is (= (:status response) 200))
             (testing "and the body must contain the URL"
               (is (= (:body response) url))               
               (testing "and that once deleted, then indeed deleted then the id does not exist"
                 (let [response (retrieve-link strg id)] 
                   (is (= (:status response) 404))))))))

      (testing "when an unregistered id is specified"
        (let [response (delete-link strg "invalid-id")] 
           (testing "then the response must be 404 OK"
             (is (= (:status response) 422))))))))


