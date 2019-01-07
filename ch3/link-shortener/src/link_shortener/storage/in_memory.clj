(ns link-shortener.storage.in-memory
  "Implements in-memory storage as defined by the Storage protocol"  
  (:require [link-shortener.storage :refer :all]))

(def strg-atom (atom {}))

;;
;; Implements in-memory storage as defined by the Storage protocol
;;
(defrecord Memory []
  
  Storage
  (create-link [this id url]
    (when-not (contains? @strg-atom id)
      (swap! strg-atom assoc id url)
      id))

  (update-link [this id url]
    (when (contains? @strg-atom id)
      (swap! strg-atom assoc id url)
      id))

  (delete-link [this id]
    (if-let [url (get @strg-atom id)]
      (do
        (swap! strg-atom dissoc id)
        url)))

  (reset [this]
    (swap! strg-atom empty))
  
  (get-link [this id]
    (get @strg-atom id))


  (get-links [this]
    @strg-atom))


(defn get-mem-storage
  []
  (->Memory))


