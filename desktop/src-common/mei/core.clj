(ns mei.core
  (:require [play-clj.core :as play]
            [play-clj.repl :refer [e e! s s!]]           ; remove on prod
            [play-clj.g2d :as g2d]
            [mei.constants :as const]
            [mei.screens.overlay :refer [overlay-screen]]
            [mei.screens.error :refer [error-screen]]
            [mei.entities.player :as player]
            [mei.screens.utils :as screen-utils]))

(declare mei-game main-screen)

;; TODO: figure out how this works...
(play/set-screen-wrapper! (fn [screen screen-fn]
                            (try (screen-fn)
                              (catch Exception e
                                (.printStackTrace e)
                                (play/set-screen! mei-game error-screen)))))

(play/defscreen title-screen
  :on-show
  (fn [screen entities]
    ; TODO: play background music
    (play/update! screen :renderer (play/stage) :camera (play/orthographic))
    (assoc (g2d/texture "title-screen.png") :width 350 :height 300)) ;TODO: why 350?

  :on-hide
  (fn [screen entities]
    ;todo stop music playing before transitioning
    )

  :on-render
  (fn [screen entities]
    (play/clear!)
    (play/render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
      (play/key-pressed? :enter) (play/screen! main-screen :on-start-main)))

  :on-resize
  (fn [screen entities]
    (play/height! screen 300)))

(play/defscreen main-screen
  :on-show
  (fn [screen entities]
    (when (not const/DEBUG_ON) (play/music "home-music.mp3" :play :set-looping true))
    (->> (play/orthogonal-tiled-map "mei-home.tmx" (/ 1 const/pixels-per-tile))  ; insert this tiled map as the renderer for camera below
         (play/update! screen :timeline [] :camera (play/orthographic) :current-map :home :renderer))
    ; todo: create player outside of "main screen" so that we may save/load and transfer player from/to other screens
    (let [player (player/create-sprites)]
      [player])) ; vector, so that we may add more entities later

  :on-render
  (fn [screen entities]
    (play/clear!) ;  additional clear! params ...1 1 1 1 these numbers is the rgba background color
    (let [mei-player (play/find-first :player? entities)] ; there must be a player
      (play/screen! overlay-screen :on-update-health-bar :entity mei-player))
    (some->>
      (if (play/key-pressed? :r)
        (play/rewind! screen 2)
        (map (fn [entity]
               (if (:player? entity)
                 (->> entity
                      (player/move screen entities)
                      (player/prevent-move screen)
                      (player/animate screen)
                      (player/hit-spike screen)
                      (player/use-exit? screen))
                 entity))
             entities))
      (play/render! screen)
      (screen-utils/update-screen! screen)))

  :on-key-down
  (fn [screen entities]
    (cond
      ; TODO: create something generic so that other screens may reuse these controls
      (play/key-pressed? :h) (play/app! :post-runnable #(play/set-screen! mei-game main-screen overlay-screen))
      (play/key-pressed? :o) (screen-utils/update-height screen inc)
      (play/key-pressed? :i) (screen-utils/update-height screen dec)))

  :on-start-main
  (fn [screen entities]
    (play/set-screen! mei-game main-screen overlay-screen))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (play/height! screen 6)))


(play/defgame mei-game
  :on-create
  (fn [this]
    (play/set-screen! this title-screen overlay-screen)))



;;;; repl'ing

; nrepl port: 35647

;; (mei.core.desktop-launcher/-main)

;; (e! :player? main-screen :health 10)

;; (s! main-screen :height 30)

;; (play/height! main-screen 40)

; after recovering from errors...
;; (play-clj.core/on-gl (play-clj.core/set-screen! mei-game main-screen overlay-screen))

;; (-> overlay-screen :entities deref)
