(ns cljbasics.ch1-test
  (:require [clojure.test :refer :all]
            [cljbasics.ch1 :as ch1]))


(deftest square-test
  (testing "Square should return a square of a number"
    (is (= 4 (ch1/square 2)))
    (is (= 36 (ch1/square 6)))))


(deftest sum-of-squares-test
  (testing "Sum of squares"
    (is (= 4 (ch1/sum-of-squares 0 2)))
    (is (= 5 (ch1/sum-of-squares 1 2)))
    (is (= 8 (ch1/sum-of-squares 2 2)))
    (is (= 74 (ch1/sum-of-squares 5 7)))
    (is (= 865 (ch1/sum-of-squares 17 24)))))


(deftest is-stackblowing-even?-test
  (testing "Correctness of is-stackblowing-even?"
    (is (= (ch1/is-stackblowing-even? 0) true))
    (is (= (ch1/is-stackblowing-even? 1) false))
    (is (= (ch1/is-stackblowing-even? 2) true))
    (is (= (ch1/is-stackblowing-even? 3) false))
    (is (= (ch1/is-stackblowing-even? 4) true))
    (is (= (ch1/is-stackblowing-even? 5) false))
    (is (= (ch1/is-stackblowing-even? 12) true))
    (is (= (ch1/is-stackblowing-even? 27) false))
    (is (= (ch1/is-stackblowing-even? 36) true))
    (is (= (ch1/is-stackblowing-even? 102) true))))

(deftest is-better-even?-test
  (testing "Correctness of trampoline based is-better-even?"
    (is (= (trampoline ch1/is-better-even? 0) true))
    (is (= (trampoline ch1/is-better-even? 1) false))
    (is (= (trampoline ch1/is-better-even? 2) true))
    (is (= (trampoline ch1/is-better-even? 3) false))
    (is (= (trampoline ch1/is-better-even? 4) true))
    (is (= (trampoline ch1/is-better-even? 5) false))
    (is (= (trampoline ch1/is-better-even? 12) true))
    (is (= (trampoline ch1/is-better-even? 27) false))
    (is (= (trampoline ch1/is-better-even? 36) true))
    (is (= (trampoline ch1/is-better-even? 102) true))))



(deftest is-even?-test
  (testing "Correctness of is-even?"
    (is (= (ch1/is-even? 0) true))
    (is (= (ch1/is-even? 1) false))
    (is (= (ch1/is-even? 2) true))
    (is (= (ch1/is-even? 3) false))
    (is (= (ch1/is-even? 4) true))
    (is (= (ch1/is-even? 5) false))
    (is (= (ch1/is-even? 12) true))
    (is (= (ch1/is-even? 27) false))
    (is (= (ch1/is-even? 36) true))
    (is (= (ch1/is-even? 102) true))))
