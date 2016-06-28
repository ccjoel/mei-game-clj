(ns mei.entities
  (:require [play-clj.core :as play]
            [play-clj.g2d :as g2d]
            [mei.util :as util]
            [mei.constants :refer [sprite-map DEBUG_ON]]))


(defn create
  [mei-textures] ; vector of [rows [cols]]
  (when DEBUG_ON (println "creating mei frames..."))

  (let [first-texture (util/texture-coords mei-textures [0 1])]
    (assoc first-texture

      :stand-up first-texture
      :stand-right (util/texture-coords mei-textures [1 1])
      :stand-down (util/texture-coords mei-textures [2 1])
      :stand-left (util/texture-coords mei-textures [3 1])

      :jump-right (util/texture-coords mei-textures [3 16])
      :jump-left (g2d/texture (util/texture-coords mei-textures [3 16]) :flip true false)

      :walk-right (g2d/animation util/duration
                                 (util/texture-action-coords mei-textures 1 [0 3])
                                 :set-play-mode (g2d/play-mode :loop-pingpong))
      :walk-left (g2d/animation util/duration
                                (util/texture-action-coords mei-textures 3 [0 3])
                                :set-play-mode (g2d/play-mode :loop-pingpong))

      :run-up (g2d/animation util/duration
                             (util/texture-action-coords mei-textures 0 [3 6])
                             :set-play-mode (g2d/play-mode :loop-pingpong))

      :run-right (g2d/animation util/duration
                                (util/texture-action-coords mei-textures 1 [3 6])
                                :set-play-mode (g2d/play-mode :loop-pingpong))

      :run-down (g2d/animation util/duration
                               (util/texture-action-coords mei-textures 2 [3 6])
                               :set-play-mode (g2d/play-mode :loop-pingpong))

      :run-left (g2d/animation util/duration
                               (util/texture-action-coords mei-textures 3 [3 6])
                               :set-play-mode (g2d/play-mode :loop-pingpong))

      :width 0.8
      :height (* (/ (-> sprite-map :mei :tile-height) (-> sprite-map :mei :tile-width)) 0.8)
      :x-velocity 0
      :y-velocity 0
      :x 19
      :y 6
      :player? true
      :health 5
      :direction :down)))


; move character
(defn move
  [{:keys [delta-time] :as screen} {:keys [x y] :as entity}]  ;can-jump?   params screen and entity

  (let [x-velocity (util/get-x-velocity entity)
        y-velocity (util/get-y-velocity entity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]

    (if (or (not= 0 x-change) (not= 0 y-change))
      (let [updated-entity (assoc entity
                             :x-velocity (util/decelerate x-velocity)
                             :y-velocity (util/decelerate y-velocity)
                             :x-change x-change
                             :y-change y-change
                             :x (+ x x-change)
                             :y (+ y y-change)
                             ;;              :can-jump? (if (> y-velocity 0) false can-jump?)
                             )]
        (when DEBUG_ON
          (when (or (not (= (:x updated-entity) (:x entity))) (not (= (:y updated-entity) (:y entity))))
            (println "Mei position | " "X:" (:x updated-entity) "Y:" (:y updated-entity))))

        updated-entity)
      entity)))


; animate character
(defn animate
  [screen {:keys [x-velocity y-velocity
                  stand-right stand-left
                  stand-up stand-down
                  jump-right jump-left
                  run-right run-left
                  run-up run-down] :as entity}]
  (let [direction (util/get-direction entity)]
    (merge entity
           (cond
             ; animations for running up/down
             (not= y-velocity 0) (cond
               (= direction :up)  (g2d/animation->texture screen run-up)
               (= direction :down)  (g2d/animation->texture screen run-down))

             ; animations for running right/left
             (not= x-velocity 0)
             (cond
               (= direction :right) (g2d/animation->texture screen run-right)
               (= direction :left)  (g2d/animation->texture screen run-left))

             ; animations for standing
             :else
             (cond
               (= direction :up) stand-up
               (= direction :right) stand-right
               (= direction :down) stand-down
               (= direction :left) stand-left))
           {:direction direction})))


; prevent move when touching walls.
(defn prevent-move
  [screen {:keys [x y x-change y-change] :as entity}]
  (let [old-x (- x x-change)
        old-y (- y y-change)
        entity-x (assoc entity :y old-y)
        entity-y (assoc entity :x old-x)
        up? (> y-change 0)]
    (merge
      entity
      (when (util/get-touching-tile screen entity-x "walls")
        {:x-velocity 0 :x-change 0 :x old-x})
      (when-let [tile (util/get-touching-tile screen entity-y "walls")]
        {:y-velocity 0 :y-change 0 :y old-y}))))
