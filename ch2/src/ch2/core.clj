(ns ch2.core)

(defn now
  "Returns current time with the format dd.MM.yy hh:mm:ss"
  []
  (let [date (new java.util.Date)
        formatter (java.text.SimpleDateFormat. "dd.MM.yyy hh:mm:ss") ]
    (.format formatter date)))

;; Application state
;; Stores unique ids
(def id-atom (atom 0))


;; Generate next unique id
(defn next-id
  "Returns the next unique identifier"
  []
  (swap! id-atom inc))

;; Record defining a tasks
(defrecord Task [title description creation-time])


;; Task atom. Stores tasks in a sorted map 
(def tasks (atom (sorted-map)))

;; Returns all tasks in the list
(defn get-tasks
  "Return all tasks in the to-do list"
  [category]
  (get @tasks category))

;; Get a task
(defn get-task
 "Returns a task with the given identifier. Accepts a string as identifier"
 [category id]
  (get-in  @tasks [category id]))

;; Add a task to the to-do list
(defn add-task
  "Add a task decribed by title and description to the category of the todo list. 
  Accepts a category name, title and description as strings"
  [cat ttl desc]
  (swap! tasks assoc-in [cat (next-id)] (Task. ttl desc (now))))

;; Remove  task from to-do list
(defn remove-task
  "Removes a task from the to-do list if the specied id exists. 
   The list to remove is identieid bt the specified identifie"
  [cat id]
  (swap! tasks update-in [cat] dissoc id))


;; Remove a category
(defn remove-category
  "Remove a category of tasks. All tasks that belong to the category will 
  will be removes as well"
  [cat]
  (swap! tasks dissoc cat))
