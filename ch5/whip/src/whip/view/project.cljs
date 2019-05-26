(ns whip.view.project
  (:require [reagent.core :as reagent]))


(defn render-projects
  [app-state]
  [:div
   (into [:ul]
         (for [[id {:keys [title]}] (:projects @app-state)]
           [:li title]))])
