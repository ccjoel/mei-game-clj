(ns mei.core
  (:require [play-clj.core :as play :refer :all]
            [play-clj.g2d :as g2d :refer :all]   ;funcs for 2D games
            [play-clj.ui :as ui]  ;ui code (labels.. etc)
            [mei.constants :refer [sprite-map]]
            [play-clj.repl :refer [e e! s s!]]
            [mei.entities :as me]
            [mei.util :as util]))

(declare mei-game main-screen text-screen) ; declare to use before defining

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y height me?]} entities] ; doseq for side effects, for to return values
    (when me?
      (position! screen x y)))  ; position screen to follow player
  entities)


(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)
    (ui/label "Error!" (color :white))
    (render! screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! mei-game blank-screen)))))


(defscreen main-screen
  :on-show
  (fn [screen entities]
    (music "home-music.mp3" :play :set-looping true)
    (->> (orthogonal-tiled-map "mei-home.tmx" (/ 1 util/pixels-per-tile))
         (update! screen :timeline [] :camera (orthographic) :renderer))
    (let [sheet (texture "mei.png")
          tiles (texture! sheet :split (-> sprite-map :mei :tile-width) (-> sprite-map :mei :tile-height))
          mei-images (vec (for [row (range (-> sprite-map :mei :tile-rows))]
                            (vec (for [col (range (-> sprite-map :mei :tile-cols))]
                                   (texture (aget tiles row col))))))]
      (me/create mei-images)))

  :on-render
  (fn [screen entities]
    (clear!) ;  additional clear! params ...1 1 1 1 these numbers is the rgba background color
    (some->>
      (if (or (key-pressed? :r))
        (rewind! screen 2)
        (map (fn [entity]
               (->> entity
                    (me/move screen)
                    (me/prevent-move screen)
                    (me/animate screen)))
             entities))
      (render! screen)
      (update-screen! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen 6)))


(defscreen text-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (assoc (ui/label "0" (color :white))
           :id :fps
           :x 5))

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (ui/label! :set-text (str (game :fps))))
             entity))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (height! screen 500)))


(defgame mei-game
  :on-create
  (fn [this]
    (set-screen! this main-screen text-screen)))



;;;; repl'ing

; nrepl port: 35647

;; (mei.core.desktop-launcher/-main)

;; (on-gl (set-screen! mei-game main-screen text-screen))

;; (e! identity main-screen :x 15 :y 22)

;; (s! main-screen :height 30)

;; (height! main-screen 40)
