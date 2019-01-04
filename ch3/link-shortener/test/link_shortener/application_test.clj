(ns link-shortener.application-test
  (:require [link-shortener.application :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))


(deftest test-app
  (testing "application routes"
    (testing "main get route"
      (let [response (main-handler (mock/request :get "/"))]
        (testing "that status is 200"
          (is (= (:status response) 200)))
        (testing "that the response body contains expected results"
          (is (= (:body response) "Link Shortner - v0.0.1")))))
    (testing "not-found route"
      (let [response (main-handler (mock/request :get "/invalid-route"))]
        (testing "that status code is a 404"
          (is (= (:status response) 404)))
        (testing "that error message is as epected"
          (is (= (:body response) "Invalid request!")))))))
