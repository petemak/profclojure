(ns link-shortener.storage-test
  (:require [clojure.test :refer :all]
            [link-shortener.storage :refer :all]
            [link-shortener.storage.in-memory :refer :all]))



(def strg (->Memory))


;; fixture will be passed a fn that will call all tests
(defn storage-test-fixture
  "Wrap test runs with a fixture function to perform setup and
   teardown: in this case to clear the storage"
  [f]
  (delete-links strg)
  (f))

;; Register fixture to be called before every
;; test run
(use-fixtures :each storage-test-fixture)

(deftest storage-create-test
  "Tests fullfilment of storage contract for creating links"
  (let [test-id   "test-id"
        test-link "https://test.domain.com/clojure"] 
    (testing "creation of links with create-link"
      (testing "that creation of links works and returns id"
        (is (= (create-link strg test-id test-link) test-id))
        (is (= (get-link strg test-id) test-link))
        (testing "and if the id exists then it will not be overwritten"
          (is (nil? (create-link strg test-id test-link)))))) 
    (testing "Update of existing links"
      (let [new-link "https://newlink.domain.com/clojure"]
        (testing "that if id exists then its returned"
          (is (= (update-link strg test-id new-link) test-id)))
        (testing "that if id is not in use then nil is returned"
          (is (nil? (update-link strg "not-existent-id" new-link))))))))


(deftest storage-list-test
  "Tests fullfilment of storage contract for listing links"
  (testing "listing of URLs"
    (let [id-url-map {"id1" "https://xyz.example.com/id1"
                      "id2" "https://xyz.example.com/id2"
                      "id3" "https://xyz.example.com/id3"}
          ids (doseq [[an-id a-url] id-url-map]
                (create-link strg an-id a-url))
          links (get-links strg)]
      (testing "that a list of links is returned"
        (is (= (count links) 3)))
      (testing "links resturned are what was entered"
        (is (= links id-url-map))))))


(deftest storage-delete-link-test
  "Tests fullfilment of storage contract for deletion"
  (let [test-id   "del-test-id"
        test-link "https://test.domain.com/clojure"] 
    (testing "deletion of existing links"
      (delete-links strg)
      (let [id (create-link strg test-id test-link)] 
        (testing "that deleting a link returns its deleted url"
          (is (= (delete-link strg test-id) )) 
          (testing "and that once deleted, then  indeed deleted then the id does not exist"
            (is (nil? (get-link strg test-id)))
            (is (= (count (get-links strg)) 0))))))))



(deftest storage-delete-links-test
  "Testing fullfilment of delete links contract"
  (testing "Deletion of all URLs"
    (let [id-url-map {"del-id1" "https://xyz.example.com/id1"
                      "del-id2" "https://xyz.example.com/id2"
                      "del-id3" "https://xyz.example.com/id3"}
          ids (doseq [[an-id a-url] id-url-map]
                (create-link strg an-id a-url))]
      (testing "that deleting all clears the storage"
        (let [links (get-links strg)] 
          (is (= (count links) 3))
          (is (= (count (delete-links strg)) 0)))))))
