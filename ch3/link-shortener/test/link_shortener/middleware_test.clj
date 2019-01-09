(ns link-shortener.handler-test
  (:require [link-shortener.middleware :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

;; test should demonstrate that the middleware converts a body that is InputStream into
;; a string but otherwise ignores the request if the body is not or is empty
(deftest wrap-slurp-body-test
  (testing "Middleware funtcionality for removing Java InputStream"
    (let [body-string "This is the body data"
          request (mock/request :post "/test" body-string)
          expected-request (assoc request :body body-string)
          ;; A handler that will slurp the body and
          ;; simply call identiy on the request effectively
          ;; returning it
          identity-handler (wrap-slurp-body identity)]
      (testing "When given a given a body with a ByteArrayInputStream body"
        (let [wrapped-request (identity-handler request)]
          (testing "that the body contains the expects data string"
            (is (= (:body wrapped-request) body-string)
                (testing "and the rest of the request is unchanged"
                  (is (= wrapped-request expected-request)))))))

      (testing "when the request has an empty body"
        (let [empty-body-req (mock/request :get "/test")]
          (testing "that that no changes applied to request"
            (is (= (identity-handler empty-body-req) empty-body-req)))))

      (testing "when the middleware is applied multiple times"
        (let [request (mock/request :post "/test" body-string)]
          (testing "that there is no effect on the expected results"
            (is (= (-> request
                       (identity-handler)
                       (identity-handler)
                       (identity-handler))
                   expected-request))))))))
