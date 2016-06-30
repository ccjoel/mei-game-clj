(ns mei.screens.title
  (:require [play-clj.core :as play]
            [play-clj.g2d :as g2d]
            [play-clj.ui :as ui]))

(play/defscreen title-screen
  :on-render
  (fn [screen entities]
    (play/clear!)
    (ui/label "Error!" (play/color :blue))
    (play/render! screen)))
