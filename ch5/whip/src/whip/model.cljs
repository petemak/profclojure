(ns whip.model
  (:require [reagent.core :as reagent :refer [atom]]))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (reagent/atom
                    {:projects
                     {"prj001" {:title "Build Project  Manager"
                                :stories {1 {:title "Design data model for stories"
                                             :status "open"
                                             :order 1}
                                          2 {:title "Design UI"
                                             :status "open"
                                             :order 2}
                                          3 {:title "Define project structure"
                                             :status "open"}}} 
                      "prj002"  {:tile "Build Task Manager front end"
                                 :stories {}}}}))

(println "=============================================")
(println @app-state)
(println "=============================================")

