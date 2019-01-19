(ns link-shortener.storage
  "The storage namespace isolates the abstraction for the persitance layer")


(defprotocol Storage
  "The storage protocol defines an abstraction for implementing the persitance layer"
  (create-link [this id url]
    "Saves the given URL under the id and returns the id if successful. Returns nill in 
    case of failure 
    for example when the id is already in use.")
  (update-link [this id new-url]
    "Update an existing link to point to a new URL. Retuerns the id if the operation 
    was successful, 
    nil if the id does not exist.")
  (get-link [this id]
    "Returns the URl associated with the given id. Nil if the id is not not in use.")
  (get-links [this]
    "Returns a map of all stored ids and URLs")
  (delete-link [this id]
    "Removes the link with the specified id from storage if it exists. Returns 
    deleted link or nil otherwise")
  (delete-links [this]
    "Clear storage of all entries.")
  (reset [this]
    "Clear all links from storage"))
