(ns mei.util
  (:require [play-clj.core :as play]))

(def ^:const pixels-per-tile 16)
(def ^:const duration 0.15)
(def ^:const damping 2)
(def ^:const max-velocity 7)
(def ^:const deceleration 0.8)

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))

(defn ^:private get-player-velocity
  [{:keys [x-velocity y-velocity]}]
  [(cond
     (play/key-pressed? :dpad-left)
     (* -1 max-velocity)
     (play/key-pressed? :dpad-right)
     max-velocity
     :else
     x-velocity)
   (cond
     (play/key-pressed? :dpad-down)
     (* -1 max-velocity)
     (play/key-pressed? :dpad-up)
     max-velocity
     :else
     y-velocity)])

(defn get-velocity
  [entities {:keys [player?] :as entity}]
  (get-player-velocity entity))

(defn get-direction
  [{:keys [x-velocity y-velocity direction]}]
  (cond
    (not= y-velocity 0)
    (if (> y-velocity 0) :up :down)
    (not= x-velocity 0)
    (if (> x-velocity 0) :right :left)
    :else direction))


; if player is touching a tile, get it (by x , y coordinates)
(defn get-touching-tile
  [screen {:keys [x y width height]} & layer-names]
  (let [layers (map #(play/tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (play/tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))


(defn texture-coords [textures-vector [col row]]
  (nth (nth textures-vector col) row))

(defn texture-action-coords [textures-vector col [start end]]
  (subvec (nth textures-vector col) start end))
