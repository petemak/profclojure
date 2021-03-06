(ns whip.view.navbar
  (:require [reagent.core :as reagent]))


(defn user-menu
  "Render user menu depending if logged in
   otherwise show Login"
  [app-state]
  (if-let [username (:username @app-state)]
       [:a {:href "#/settings"} (:username @app-state)]
       [:a {:href "#/login"} "Login"]) )

(defn nav-bar
  "Draws the vavigation bar---  Whip      About Peter/Login"
  [app-state]
  [:nav
   [:ul.nav-list
    [:li
     [:a {:href "#/"}
      [:img {:src "img/logo.png"}]
      [:span {:style {:font-family "fantasy"}} "Whip Project Management Tool"]]] 
    [:ul.nav-list {:style {:float "right"}}
     [:li [:a {:href "#/"} "About"]]
     [:li [user-menu app-state]]]]])
