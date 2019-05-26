(ns whip.view.story
  (:require [reagent.core :as reagent]))


(defn render-story
  "Render story sid from the project specified pid"
  [app-state  pid sid {:keys [title status]}]
  [:li.card
   (if (= status "done")
     [:del title]
     [:span
      title
      " - "
      [:button
       {:on-click (fn del-clicked
                    [e]
                    (swap! app-state assoc-in [:projects pid :stories sid :status] "done"))}
       "done"] ])])

(defn render-stories
  "Render stories from the projects
   with is pid"
  [app-state pid]
  (into [:ul]
        (for [[sid story] (get-in @app-state [:projects pid :stories])]
          (render-story app-state pid sid story)))) 

