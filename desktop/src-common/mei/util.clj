(ns mei.util
  (:require [play-clj.core :refer :all]))

(def ^:const vertical-tiles 43)
(def ^:const pixels-per-tile 16)
(def ^:const duration 0.15)
(def ^:const damping 0.5)
(def ^:const max-velocity 14)
(def ^:const max-jump-velocity (* max-velocity 4))
(def ^:const deceleration 0.9)
(def ^:const gravity -2.5)

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))


(defn get-x-velocity
  [{:keys [me? x-velocity]}]
  (if me?
    (cond
      (key-pressed? :dpad-left)   (- max-velocity)
      (key-pressed? :dpad-right)  max-velocity
      :else                       x-velocity)
    x-velocity))


(defn get-y-velocity
  [{:keys [me? y-velocity can-jump?]}]
  (if me?
    (cond
      (and can-jump? (key-pressed? :space))
      max-jump-velocity
      :else
      y-velocity)
    y-velocity))

; direction of movement
(defn get-direction
  [{:keys [x-velocity direction]}]
  (cond
    (> x-velocity 0) :right
    (< x-velocity 0) :left
    :else
    direction))


; if char is touching a tile, get it (by x / y)
(defn get-touching-tile
  [screen {:keys [x y width height]} & layer-names]
  (let [layers (map #(tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))


(defn texture-coords [textures-vector [col row]]
  (nth (nth textures-vector col) row))

(defn texture-action-coords [textures-vector col [start end]]
  (subvec (nth textures-vector col) start end))
