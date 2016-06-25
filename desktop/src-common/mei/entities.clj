(ns mei.entities
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [mei.util :as util]
            [clojure.edn :as edn]))

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(defn create
  [mei-textures] ; vector of [cols [rows]]
  (println "creating entities...")

  (println "number of cols" (count mei-textures))
  (println "number of rows" (count (first mei-textures)))

  (let [first-texture (util/texture-coords mei-textures [1 0])]

  (assoc first-texture
    ; assoc more!
    :stand-up first-texture
    :stand-right (util/texture-coords mei-textures [0 1])
    :stand-down (util/texture-coords mei-textures [1 2])
    :stand-left (util/texture-coords mei-textures [1 3])

    :jump-right (util/texture-coords mei-textures [16 3])
    :jump-left (texture (util/texture-coords mei-textures [16 3]) :flip true false)

    :walk-right (animation util/duration
                           (util/texture-action-coords mei-textures 3 [0 3])
;;                            walk
                           :set-play-mode (play-mode :loop-pingpong))
    :walk-left (animation util/duration
                           (util/texture-action-coords mei-textures 1 [0 3])
;;                            walk
                           :set-play-mode (play-mode :loop-pingpong))

;;     (animation util/duration
;;                           (map #(texture % :flip true false) walk)
;;                           :set-play-mode (play-mode :loop-pingpong))

    :width 1
    :height (/ (-> sprite-map :mei :tile-height) (-> sprite-map :mei :tile-width))
    :x-velocity 0
    :y-velocity 0
    :x 20
    :y 10
    :me? true
    :can-jump? false
    :direction :up))

  )

(defn move
  [{:keys [delta-time]} {:keys [x y can-jump?] :as entity}]
  (let [x-velocity (util/get-x-velocity entity)
        y-velocity (+ (util/get-y-velocity entity) util/gravity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change) (not= 0 y-change))
      (assoc entity
             :x-velocity (util/decelerate x-velocity)
             :y-velocity (util/decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change)
             :can-jump? (if (> y-velocity 0) false can-jump?))
      entity)))

(defn animate
  [screen {:keys [x-velocity y-velocity
                  stand-right stand-left
                  jump-right jump-left
                  walk-right walk-left] :as entity}]
  (let [direction (util/get-direction entity)]
    (merge entity
           (cond
             (not= y-velocity 0)
             (if (= direction :right) jump-right jump-left)
             (not= x-velocity 0)
             (if (= direction :right)
               (animation->texture screen walk-right)
               (animation->texture screen walk-left))
             :else
             (if (= direction :right) stand-right stand-left))
           {:direction direction})))

(defn prevent-move
  [screen {:keys [x y x-change y-change] :as entity}]
  (let [old-x (- x x-change)
        old-y (- y y-change)
        entity-x (assoc entity :y old-y)
        entity-y (assoc entity :x old-x)
        up? (> y-change 0)]
    (merge entity
           (when (util/get-touching-tile screen entity-x "walls")
             {:x-velocity 0 :x-change 0 :x old-x})
           (when-let [tile (util/get-touching-tile screen entity-y "walls")]
             {:y-velocity 0 :y-change 0 :y old-y
              :can-jump? (not up?) :to-destroy (when up? tile)}))))
