(ns cljbasics.ch1
  (:require [clojure.string :as s]))


;; Use substitution principal referencial transparency
;;
;; sum-of-squares:  (+ (sguare a) (square b))

(defn square
  "Square or multiply number by itsellf"
  [x]
  (* x x))



(defn sum-of-squares
 "Take two numbers and return the sum of their squares"
 [a b]
 (+ (square a) (square b)))



;; Default implementaion
;;  Takes longer as n increases
(defn fib
  "Fibonachi sequence is the series of numbers
   0 1 1 2 3 5 8 13 21 34 55...
   The next number is found by adding up the two numbers before it."
  [n]
  (condp = n
    0 0
    1 1
    (+ (fib (- n 1))
       (fib (- n 2)))))



;; Memoized version
;; Note: the memoized function is a ref
;;
;; First execution:
;; (time (ch1/memoized-fib 42))
;; "Elapsed time: 82477.769378 msecs"
;; 267914296
;;
;; Second executaion is significatly faster
;; (time (ch1/memoized-fib 42))
;; "Elapsed time: 0.036744 msecs"
;;267914296
;;
(def memoized-fib
  (memoize (fn [n]
             (condp = n
               0 0
               1 1
               (+ (fib (- n 1))
                  (fib (- n 2)))))))



;; Factorial: the factorial of any number is that number
;; times the factorial of that number minus 1
;; 
;; n! = n × (n−1)! 
;;
(defn factorial
  "n! = n × (n−1)!"
  [n]
  (loop [acc 1
         cnt n]
    (if (<= cnt 1)
      acc
      (recur (* acc cnt) (dec cnt)))))



;; Mutual recursion
;; 
;;The naive implementation can blow the stack
;; Each recursive call stores state on the stack

(declare is-stackblowing-even? is-stackblowing-odd?)

;; A number is even when the decrement is odd
;;
;; Naive implementation, blows stack!!!
;; (ch1/is-stackblowing-even? 100101)
;; StackOverflowError   clojure.lang.Numbers$LongOps.combine (Numbers.java:419)
(defn is-stackblowing-even?
  "Returns true if the nuber n is even
   mutually calls is-odd? on decrementns of n 
   unitl 0 which is even.

   This version blows the stack:
   > (ch1/is-stackblowing-even? 100101)
   > StackOverflowError   clojure.lang.Numbers$LongOps.combine (Numbers.java:419)"
  [n]
  (if (= n 0)
    true
    (is-stackblowing-odd? (dec n))))


(defn is-stackblowing-odd?
  "Returns true if number n is odd 
   mutually calls is-odd? on decrementns of n 
   unitl 0 which not odd"
  [n]
  (if (= n 0)
    false
    (is-stackblowing-even? (dec n))))

;;
;; trampoline solves the problem
;;
;; trampoline can be used to convert algorithms requiring
;; mutual recursion without stack consumption. 
;; Functions must return functions mutually calling
;; each other with a decremento
;; 
(declare is-better-even? is-better-odd?)

(defn is-better-even?
  "Trampoline version: returns a function that returs true if the nuber n is even
   mutually calls is-odd? on decrementns of n 
   unitl 0 which is even 

   The trampoline version does not blow the stack
   > (trampoline ch1/is-even? 1002001)
   > false"
  [n]
  (if (= n 0)
    true
    #(is-better-odd? (dec n))))


(defn is-better-odd?
  "Returns true if number n is odd 
   mutually calls is-odd? on decrementns of n 
   unitl 0 which not odd"
  [n]
  (if (= n 0)
    false
    #(is-better-even? (dec n))))



;; Above requires knowledge of trampoline
;; can be e-writen as follows
(defn is-even?
  "Returns true if number n is even 
   uses trampoline to mutually calls even and odd functions
   on decrementns of n 
   unitl 0 which not odd"
  [n]
  (letfn [(e? [n]
            (if (= n 0)
              true
              (o? (dec n))))
          (o? [n]
            (if (= n 0)
              false
              (e? (dec n))))]
    (trampoline e? n)))



(defn is-odd?
  "Returns true if number n is odd 
   negates response from is-even?"
  [n]
  (not (is-even? n)))


;; Function to show laziness
;; Not a good example of pure functions but should print out when called

(defn print-inc
  [i]
  (println i)
  (inc i))

;; The bove can be invoked in a map function on a sequence of
;; numbers but will not print out as expected
;; user> (def res (map ch1/print-inc (range 10)))
;; #'user/res
;;
;; Only after referenceing res is the sequence realised.
;; > res
;; 0
;; 1
;; 2
;; 3
;; 4
;; 5
;; 6
;; 7
;; 8
;; 9
;; (1 2 3 4 5 6 7 8 9 10)


(def names ["asa" "dsfs" "wtrert" "werwer" "lskejk" "rensd" "sdfsdf"
            "werertwertewrt" "jfj" "wweree" "orie" "jajeoas" "nkdjjdjs"
            "qhhhwer" "jweru" "asjdf" "webbbr" "teoosd" "hdfOOE" "nwfj[sdkf"
            "mmembdf" "ikaejbas" "jerjt" "weert" "memrmt" "kkdlsd"
            "yryye" "wbber"])
(defn create-mapping
  [ns]
  (mapv #(vector %1 %2) (cycle [:first :second :third :fourth]) ns))



;;
;; (create-mapping names)
;;


;; MUTATION

;;
;; Atoms - manage asingle piece of shared state
;; in a synchrous but uncoordinated manner
(def app-state (atom {}))

;;
;; add user
;; Update atom using swap!
(defn add-user
  "Add the user with specidied id and 
  first name to the app state"
  [id name]
  (swap! app-state assoc-in [:users id :first-name] name))


(defn first-name
  "Return the first name of the user with given id"
  [id]
  (get-in @app-state [:users id :first-name]))


(defn clear-state
  "Clears the application state of all entries"
  []
  ((reset! app-state)))



;;
;; Refs - transaction references. Used to coordinate changes
;; between multiple objects
;;
;; Refs use STML (Software Transactional Memmory) to coordinate transactions
;; and fullfil traditional ACI(D)
;;
;; Example coordination of a transaction between two accounts
;;
(def savings-account (ref {:balance 250}))
(def checking-account (ref {:balance 700}))

(defn simulate-error
  "Simulates an error condition by trowing an exception with the 
 given message"
  [msg]
  (throw (Exception. msg)))
  
;;
;; Simulates a failed transaction. The balance should not change
;; 
(defn save-snap
  "Transfer amount a from the checking account c to the savings account s"
  [amount]
  (dosync
    (let [checking-before (:balance @checking-account)
          savings-before (:balance @savings-account)
          checking-after (- checking-before amount)
          savings-after (+ savings-before amount)] 
      (commute checking-account assoc :balance  checking-after)
      (simulate-error "Oh snap...")
      (commute savings-account assoc :balance savings-after))))


;; Transfer amount to savings account
;; user> (ch1/save 250)
;; {:balance 500}
;; user> ch1/checking-account
;; #ref[{:status :ready, :val {:balance 450}} 0x5edf3030]
;; user> ch1/savings-account
;; #ref[{:status :ready, :val {:balance 500}} 0x2f4e16c7]

(defn save
  "Transfer amount a from the checking account c to the savings account s"
  [amount]
  (dosync
    (let [checking-before (:balance @checking-account)
          savings-before (:balance @savings-account)
          checking-after (- checking-before amount)
          savings-after (+ savings-before amount)] 
      (commute checking-account assoc :balance  checking-after)
      (commute savings-account assoc :balance savings-after))))


;;
;; Runtime dispatch
;; The basic idea behind runtime polymorphism is that
;; a single function designator dispatches to
;; multiple independently-defined function definitions
;; based upon some value of the call.
;; Multimethods are defined using defmulti, which takes
;; 1) the name of the multimethod and
;; 2) the dispatch function.
(defmulti encounter (fn [x y]
                      [(:Species x)
                       (:Species y)]))

;; Methods are independently defined using defmethod, passing:
;; 1) the multimethod name,
;; 2) the dispatch value and
;; 3) the function body.
(defmethod encounter [:Bunny :Bunny] [b1 b2] :mate)
(defmethod encounter [:Bunny :Lion] [b l] :run-away)
(defmethod encounter [:Lion :Lion] [l1 l2] :fight)
(defmethod encounter [:Lion :Bunny] [l b] :eat)

;; Data
(def b1 {:Species :Bunny :other :stuff})
(def b2 {:Species :Bunny :other :stuff})
(def l1 {:Species :Lion :other :stuff})
(def l2 {:Species :Lion :other :stuff})

;; call
(encounter b1 l1) ;; :run-away
(encounter b2 l2) ;; :run-away
(encounter b1 b2) ;; :mate 
(encounter l1 b1) ;; :eat
(encounter l2 b2) ;; :eat
(encounter l2 l2) ;; fight



;; Multimenthod for generic area
;; calculation of geometric shapes
(defmulti area (fn [shape & _]
                 shape))

;;Methods
;; 1. triangle
(defmethod area :triangle
  [_ width height]
  (/ (* width height) 2))


;; 2. Rectangle
(defmethod area :rectangle
  [_ length width]
  (* length width))

;; 3. Square
(defmethod area :square
  [_ side]
  (* side side))


;; 4. Circle
(defmethod area :circle
  [_ rad]
  (* Math/PI rad rad))


;;------------------------
;; 5% New York
;; 4.5% California
(def ny-invoice
  {:d 42
   :issue-date "2016-01-01"
   :due-date "2016-02-01"
   :customer {:name "Foo Bar Industries"
              :address "123 Main St"
              :city "New York"
              :state "NY"
              :zipcode "10101"}
   :amount-code 5000})


(def ca-invoice
  {:d 42
   :issue-date "2017-02-16"
   :due-date "2016-03-16"
   :customer {:name "Bar Foo Enterprise"
              :address "456 Rodeo Drive"
              :city "Los Angeles"
              :state "CA"
              :zipcode "72301"}
   :amount-code 5000})


(defmulti calc-invoice
  (fn [invoice]
    (-> (get-in invoice [:customer :state])
        keyword)))


(defmethod calc-invoice :NY
  [invoice]
  (let [amount-due (:amount-code invoice)]
    (+ amount-due (* amount-due 0.5))))


(defmethod calc-invoice :CA
  [invoice]
  (let [amount-due (:amount-code invoice)]
    (+ amount-due (* amount-due 0.45))))


;; Records and protocols
(defprotocol Shape
  (area2 [this])
  (perimeter [this]))



(defrecord Circle [radius]
  Shape
  (area2 [this]
    (* Math/PI (* (:radius this) (:radius this))))
  (perimeter [this]
    (* 2 Math/PI (:radius this))))

;; Use record
;;(def c (->Circle 7))
;;(area2 c)
;;(perimeter c)
;;(:radius c)



;;
;; Persistent data structures

(defprotocol INode
  "Binary search tree node"
  (value [_] "Value of a node")
  (left [_] "left ndoe")
  (right [_] "Right node")
  (contains-value? [_ _] "Return true if a value of contained in the tree")
  (insert-value [_ _] "Insert a value intot the tree"))


(deftype Node [node-value left-node right-node]
  INode
  (value [_] node-value)
  (left [_] left-node)
  (right [_] right-node)
  (contains-value? [node v]
    (cond
      (nil? node) false
      (= v node-value) true
      (< v node-value) (contains-value? left-node v)
      (> v node-value) (contains-value? right-node v)))
  (insert-value [node v]
    (cond
      (nil? node) (Node. v nil nil)
      (= v node-value) node
      (< v node-value) (insert-value left-node v)
      (> v node-value) (insert-value right-node v))))

;; Ensures that null nodes return values
;;
;;    No implementation of method: :contains-value? of protocol:
;;   #'cljbasics.ch1/INode found for class: nil
(extend-protocol INode
  nil
  (value [_]  nil)
  (left [_] nil)
  (right [_n] nil)
  (contains-value? [node v] nil)
  (insert-value [node v] (Node. v nil nil)))

;; Test node
(def n (Node. 7 nil nil))

;; Should return 7
(value n)

;; Left is ni
(left n)
(right n)

;; Contains 7 sholud return true
(contains-value? n 7)


;; Contains 2 will crash if nil is not extended
;;    No implementation of method: :contains-value? of protocol:
;;   #'cljbasics.ch1/INode found for class: nil
(contains-value? n 2)



(def r (Node. 7 (Node. 5  (Node. 3 nil nil) nil)
              (Node. 12 (Node. 9 nil nil) (Node. 17 nil nil))))



(left r)


;; 5
(-> r
    left
    value)

;;17
(-> r
    right
    right
    value)

;; Add node 6 
(def r2 (insert-value (left r) 6))

;; Check if they are identicle
;; False
;;
;; user> ch1/r

;; #object[cljbasics.ch1.Node 0x5a52d541 "cljbasics.ch1.Node@5a52d541"]
;; user> ch1/r2
;; #object[cljbasics.ch1.Node 0x55bc5ebb "cljbasics.ch1.Node@55bc5ebb"]
(identical? r r2)
