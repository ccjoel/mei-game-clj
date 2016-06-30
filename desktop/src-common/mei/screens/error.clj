(ns mei.screens.error
  (:require [play-clj.core :as play]
            [play-clj.ui :as ui]))

(play/defscreen error-screen ; screen to show when errors present
  :on-show
  (fn [screen entities]
    (play/update! screen :renderer (play/stage) :camera (play/orthographic))
    (ui/label "Error!" (play/color :white))))
