(ns mei.screens.overlay
  (:require [play-clj.core :as play]
            [play-clj.ui :as ui]
            [play-clj.g2d :as g2d]))

(defn- create-one-health-heart [x]
  (assoc (g2d/texture "heart.png") :x x :y 265 :health? true :hid x :width 35 :height 30))

(defn create-player-health [number-hearts]
  (loop [x 1 hearts '()]
    (if (= (count hearts) number-hearts)
      hearts
      (recur (+ x 10) (cons (create-one-health-heart x) hearts) ))))

(play/defscreen overlay-screen
  :on-show
  (fn [screen entities]
    (play/update! screen :camera (play/orthographic) :renderer (play/stage))
    [(assoc (ui/label "0" (play/color :white)) :id :fps :x 5)
     (assoc (ui/label "0" (play/color :white)) :id :time :x 290)])

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (ui/label! :set-text (str (play/game :fps))))
             :time (doto entity (ui/label! :set-text (str (int (:total-time screen)))))
             entity))
         (play/render! screen)))

  ; custom function that is invoked from screens where the player is preset
  :on-update-health-bar
  (fn [screen entities]
    (concat
      ;TODO: optimize this. remove the previous hearts while looping elsewhere, so that we dont loop twice.
      (remove :health? entities) ; TODO: bug here? Sometimes hearts get removed permanently from screen.
      (create-player-health (:health (:entity screen)))))

  :on-resize
  (fn [screen entities]
    (play/height! screen 300)))
