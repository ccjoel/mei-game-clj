(ns mei.core
  (:require [play-clj.core :as play]
            [play-clj.g2d :as g2d]                       ; funcs for 2D games
            [play-clj.ui :as ui]                         ; ui code (labels.. etc)
            [mei.constants :as const]
            [play-clj.repl :refer [e e! s s!]]           ; remove on prod
            [mei.entities :as me]
            [mei.util :as util]))

(declare mei-game main-screen text-screen) ; declare to use before defining

(defn- update-height [screen update-fn]
  (let [new-height (update-fn (play/height screen))]
    (when const/DEBUG_ON (println "New height: " new-height))
    (play/height! screen new-height)))

(defn update-screen!
  "Updates screen / camera to follow player when moving around"
  [screen entities]
  (doseq [{:keys [x y height player?]} entities] ; doseq for side effects, for to return values
    (when player?
      (play/position! screen x y)))
  entities)

(play/defscreen blank-screen ; screen to show when errors present
  :on-render
  (fn [screen entities]
    (play/clear!)
    (ui/label "Error!" (play/color :white))
    (play/render! screen)))

(play/set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (play/set-screen! mei-game blank-screen)))))

(defn- create-player-sprites []
  (let [sheet (g2d/texture "mei.png")
        tiles (g2d/texture! sheet :split (-> const/sprite-map :mei :tile-width) (-> const/sprite-map :mei :tile-height))
        mei-images (vec (for [row (range (-> const/sprite-map :mei :tile-rows))]
                          (vec (for [col (range (-> const/sprite-map :mei :tile-cols))]
                                 (g2d/texture (aget tiles row col))))))]
    (me/create mei-images)))

(defn- create-player-health []
  (assoc (g2d/texture "heart.png") :x 1 :y 265 :health? true :width 35 :height 30))

(play/defscreen main-screen
  :on-show
  (fn [screen entities]
    (when (not const/DEBUG_ON) (play/music "home-music.mp3" :play :set-looping true))
    (->> (play/orthogonal-tiled-map "mei-home.tmx" (/ 1 util/pixels-per-tile))  ; insert this tiled map as the renderer for camera below
         (play/update! screen :timeline [] :camera (play/orthographic) :renderer))
    (let [player (create-player-sprites)]
      ; vector, so that we may add more entities later
      [player]))

  :on-render
  (fn [screen entities]
    (play/clear!) ;  additional clear! params ...1 1 1 1 these numbers is the rgba background color
    (some->>
      (if (play/key-pressed? :r)
        (play/rewind! screen 2)
        (map (fn [entity]
               (if (:player? entity)
                 (->> entity
                      (me/move screen)
                      (me/prevent-move screen)
                      (me/animate screen))
                 entity))
             entities))
      (play/render! screen)
      (update-screen! screen)))

  ; add on key press to handle restart, forward? and other keyboard behaviors... such as zoom out and in of map (up to a limit)
  :on-key-down
  (fn [screen entities]
    (cond
      (play/key-pressed? :h) (play/app! :post-runnable #(play/set-screen! mei-game main-screen text-screen))
      (play/key-pressed? :o) (update-height screen inc)
      (play/key-pressed? :i) (update-height screen dec)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (play/height! screen 6)))


(play/defscreen text-screen
  :on-show
  (fn [screen entities]
    (play/update! screen :camera (play/orthographic) :renderer (play/stage))
    [(assoc (ui/label "0" (play/color :white)) :id :fps :x 5)
     (create-player-health)])

  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (ui/label! :set-text (str (play/game :fps))))
             entity))
         (play/render! screen)))

  :on-resize
  (fn [screen entities]
    (play/height! screen 300))) ; TODO: debug this height


(play/defgame mei-game
  :on-create
  (fn [this]
    (play/set-screen! this main-screen text-screen)))



;;;; repl'ing

; nrepl port: 35647

;; (mei.core.desktop-launcher/-main)

;; (e! :health? text-screen :width 35 :height 30)

;; (s! main-screen :height 30)

;; (play/height! main-screen 40)

; after recovering from errors...
;; (play-clj.core/on-gl (play-clj.core/set-screen! mei-game main-screen text-screen))

;; (-> text-screen :entities deref)
