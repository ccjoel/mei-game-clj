(ns mei.core
  (:require [play-clj.core :as play :refer :all]
            [play-clj.g2d :as g2d :refer :all]   ;funcs for 2D games
            [play-clj.ui :as ui]  ;ui code (labels.. etc)
            [mei.constants :refer [sprite-map DEBUG_ON]]
            [play-clj.repl :refer [e e! s s!]]
            [mei.entities :as me]
            [mei.util :as util]))

(declare mei-game main-screen text-screen) ; declare to use before defining

(defn update-screen!
  "Updates screen / camera to follow player when moving around"
  [screen entities]
  (doseq [{:keys [x y height me?]} entities] ; doseq for side effects, for to return values
    (when me? (play/position! screen x y)))
  entities)


(defscreen blank-screen ; screen to show when errors present
  :on-render
  (fn [screen entities]
    (play/clear!)
    (ui/label "Error!" (color :white))
    (play/render! screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (play/set-screen! mei-game blank-screen)))))

(defn- create-player-sprites []
  (let [sheet (texture "mei.png")
        tiles (texture! sheet :split (-> sprite-map :mei :tile-width) (-> sprite-map :mei :tile-height))
        mei-images (vec (for [row (range (-> sprite-map :mei :tile-rows))]
                          (vec (for [col (range (-> sprite-map :mei :tile-cols))]
                                 (texture (aget tiles row col))))))]
    (me/create mei-images)))


(defscreen main-screen
  :on-show
  (fn [screen entities]
    (when (not DEBUG_ON) (music "home-music.mp3" :play :set-looping true))
    (->> (orthogonal-tiled-map "mei-home.tmx" (/ 1 util/pixels-per-tile))  ; insert this tiled map as the renderer for camera below
         (update! screen :timeline [] :camera (orthographic) :renderer))
    (create-player-sprites))

  :on-render
  (fn [screen entities]
    (clear!) ;  additional clear! params ...1 1 1 1 these numbers is the rgba background color
    (some->>
      (if (key-pressed? :r)
        (rewind! screen 2)
        (map (fn [entity]
               (->> entity
                    (me/move screen)
                    (me/prevent-move screen)
                    (me/animate screen)))
             entities))
      (render! screen)
      (update-screen! screen)))

  ; add on key press to handle restart, forward? and other keyboard behaviors... such as zoom out and in of map (up to a limit)
  :on-key-down
  (fn [screen entities]
    (cond
      (key-pressed? :h) (app! :post-runnable #(set-screen! mei-game main-screen text-screen))))

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
    (height! screen 500))) ; TODO: debug this height


(defgame mei-game
  :on-create
  (fn [this]
    (set-screen! this main-screen text-screen)))



;;;; repl'ing

; nrepl port: 35647

;; (mei.core.desktop-launcher/-main)

;; (on-gl (set-screen! mei-game main-screen text-screen))

;; (e! identity main-screen :x 19 :y 7.2)

;; (s! main-screen :height 30)

;; (height! main-screen 40)
