(ns whip.main
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as dom]
            [whip.model :as model]
            [bidi.bidi :as bidi]
            [whip.view.navbar :as navbar]
            [whip.view.story :as story]
            [whip.view.projects :as projects]))

(enable-console-print!)

(defn app-gui
  [app-state]
  (let [active-route (:route @app-state)
        {:keys [active-handler route-params]} (bidi/match-route routes active-route)]
    [(or active-handler (projects/project-list))]))
  
  (defn app-ui []
    [:div.content
     [:div.header
      [navbar/nav-bar model/app-state]]
     [:div.main 
      [story/render-stories model/app-state "prj001"]]
     [:div.footer
      {:style {:font-family "fantasy"}}
      [:center "~ All Credits to Professional Clojure - ISBN 978-1-119-26727-0 ~"]]])

(when-let [app (dom/getElement "app")]
  (reagent/render-component [app-ui] app))

  
  (defn on-js-reload []
    ;; optionally touch your app-state to force rerendering depending on
    ;; your application
    ;; (swap! app-state update-in [:__figwheel_counter] inc)
    )

