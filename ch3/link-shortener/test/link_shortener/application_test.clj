(ns link-shortener.application-test
  (:require [link-shortener.application :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))


(deftest test-app
  (testing "application routes"
    (testing "invalid get route"
      (let [response (app-handler (mock/request :get "/"))]
        (testing "that status is 404"
          (is (= (:status response) 404)))))
    (testing "not-found route"
      (let [response (app-handler (mock/request :get "/invalid-route"))]
        (testing "that status code is a 404"
          (is (= (:status response) 404)))
        (testing "that error message is as epected"
          (is (= (:body response) "Not nFound")))))))
