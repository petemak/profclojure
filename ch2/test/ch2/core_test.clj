(ns ch2.core-test
  (:require [ch2.core :as ch2]
            [clojure.test :refer :all]))

;; Fixture code to run around each test in this namespace
;; 
(defn each-fixture-clear-atoms
  "Ensures that storage atoms are cleared of state
  Before each test run"
  [test]
  (reset! ch2/id-atom 0)
  (swap! ch2/tasks empty)
  (test))

;; Fixture: setup and teardown tests
;; calls clear-atoms to ensure state
;; is removed.
(use-fixtures :each each-fixture-clear-atoms)

(deftest test-nex-tid
  (testing "generation of task id: can not be nil or equal"
    (let [first-id (ch2/next-id)]
      (is (not= first-id nil))
      (is (> first-id 0)) 
      (is (not= (ch2/next-id) first-id))
      (is (not= (ch2/next-id) (ch2/next-id))))))

(deftest test-add-task
  (testing "Additon of tasks"
    (let [task-list (ch2/add-task "Work" "Datomic" "Learn Datomic queries")]
      (is (= (count task-list) 1))
      (is (= (:title (first (vals (ch2/get-tasks "Work")))) "Datomic")))))


(deftest test-get-tasks
  (testing "Expected return after adding to the tasks lists"
    (let [task-list1 (ch2/add-task "Work" "Datomic" "Learn Datomic queries")
          task-list2 (ch2/add-task "Work" "Clojure" "Learn Clojure Spec")
          task-list3 (ch2/add-task "Home" "Apartment" "Buy bookshelf")]
      
      (are [expected actual]
          (= expected actual)
        1 (count task-list1)
        1 (count task-list2)
        2 (count task-list3))

      (are [expected actual]
          (= expected actual)
        2 (count (ch2/get-tasks "Work"))
        1 (count (ch2/get-tasks "Home")))

      (are [expected actual]
          (= expected actual)
        "Datomic"   (:title (first (vals (ch2/get-tasks "Work"))))
        "Clojure"   (:title (second (vals (ch2/get-tasks "Work"))))
        "Apartment" (:title (first (vals (ch2/get-tasks "Home"))))))))
