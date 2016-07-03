(ns mei.entities.utils
  (:require  [play-clj.core :as play]
             [play-clj.g2d :as g2d]
             [mei.utils :as utils]
             [mei.constants :as const]
             [clojure.pprint :refer [pprint]]))

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
  [screen {:keys [x y width height] :as entity} & layer-names]
  (if-let [layers (map #(play/tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (play/tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))

(defn near-entity?
  [{:keys [x y id] :as e} e2 min-distance]
  (and (not= id (:id e2))
       (:npc? e2)
       (nil? (:draw-time e2))
       (> (get e2 :health 1) 0)
       (< (Math/abs ^double (- x (:x e2))) min-distance)
       (< (Math/abs ^double (- y (:y e2))) min-distance)))

(defn near-entities?
  [entities entity min-distance]
  (some #(near-entity? entity % min-distance) entities))

(defn find-id
  [entities id]
  (play/find-first #(= id (:id %)) entities))


(defn update-particle-position [particle]
  (case (:direction particle)
    :up (assoc particle :y (+ (:y particle) 0.08))
    :down (assoc particle :y (- (:y particle) 0.08))
    :right (assoc particle :x (+ (:x particle) 0.08))
    :left (assoc particle :x (- (:x particle) 0.08))))

(defn remove-particles-when-done [entities]
  (let [particles-to-remove (filter (fn [entity]
                                      (and (get entity :particle? false)
                                           (or
                                             (< (:x entity) 0)
                                             (> (:x entity) const/h-home-tiles)
                                             (< (:y entity) 0)
                                             (> (:y entity) const/v-home-tiles)))) entities)]
    (remove (set particles-to-remove) entities)))
