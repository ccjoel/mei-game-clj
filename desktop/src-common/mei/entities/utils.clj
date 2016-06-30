(ns mei.entities.utils
  (:require  [play-clj.core :as play]
             [play-clj.g2d :as g2d]
             [mei.utils :as utils]
             [mei.constants :as const]))

(defn animated-texture
  "[Side effects] Returns an animation out of multiple textures and col/rows describing the spritesheet 'coordinates'."
  [textures col rows]
  (g2d/animation const/duration
                 (utils/texture-action-coords textures col rows)
                 :set-play-mode (g2d/play-mode :loop-pingpong)))

(defn decelerate
  "Decelerates velocity. Depends on deceleration constant."
  [velocity]
  (let [updated-velocity (* velocity const/deceleration)]
    (if (< (Math/abs updated-velocity) const/damping)
      0
      updated-velocity)))

(defn get-touching-tile
  "If a character (player or npc otherwise) is touching a tile, get it (by x , y coordinates)"
  [screen {:keys [x y width height]} & layer-names]
  (let [layers (map #(play/tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (play/tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))
