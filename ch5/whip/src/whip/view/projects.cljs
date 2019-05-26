(ns whip.view.projects
  (:require [whip.model :as model]
            [goog.dom.forms :as forms]))


(defn project-list
  [app-state]
  [:div
   [:span {:data-awesome "maximus"}]
   [:h2 "Projects"]
   [:form
    {:on-submit (fn add-project [e]
                  (.preventDefault e)
                  )}]])
