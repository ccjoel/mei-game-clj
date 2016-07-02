(ns mei.entities.player
  (:require [play-clj.core :as play]
            [play-clj.g2d :as g2d]
            [mei.constants :as const]
            [mei.util :as util]
            [mei.entities.utils :as entity-utils]
            [mei.screens.utils :as screen-utils]))


(defn- get-player-velocity
  "Returns updated [x y] velocities by checking which direction
  the game pad was pressed in."
  [{:keys [x-velocity y-velocity]}]
  [(cond
     (play/key-pressed? :dpad-left)  (* -1 const/max-velocity)
     (play/key-pressed? :dpad-right) const/max-velocity
     :else                           x-velocity)
   (cond
     (play/key-pressed? :dpad-down)  (* -1 const/max-velocity)
     (play/key-pressed? :dpad-up)    const/max-velocity
     :else                           y-velocity)])


(defn get-velocity
  "Returns velocity for a character. Works on player only, for now."
  [entities {:keys [player?] :as entity}]
  (get-player-velocity entity))


(defn get-direction
  "Returns a new direction given x and y velocities and a previous direction"
  [{:keys [x-velocity y-velocity direction]}]
  (cond
    (not= y-velocity 0)  (if (> y-velocity 0) :up :down)
    (not= x-velocity 0)  (if (> x-velocity 0) :right :left)
    :else                direction))


(defn create
  "receives an n x n vector of rows x columns, and returns a texture with all
  other texture/animation states as conj'ed properties."
  [mei-textures]
  (when const/DEBUG_ON (println "creating mei frames..."))
  (let [first-texture (util/texture-coords mei-textures [0 1])]
    (assoc first-texture
      :stand-up    first-texture
      :stand-right (util/texture-coords mei-textures [1 1])
      :stand-down  (util/texture-coords mei-textures [2 1])
      :stand-left  (util/texture-coords mei-textures [3 1])
      :run-up      (entity-utils/animated-texture mei-textures 0 [3 6])
      :run-right   (entity-utils/animated-texture mei-textures 1 [3 6])
      :run-down    (entity-utils/animated-texture mei-textures 2 [3 6])
      :run-left    (entity-utils/animated-texture mei-textures 3 [3 6])
      :width 0.8
      :height (* (/ (-> const/sprite-map :mei :tile-height)
                    (-> const/sprite-map :mei :tile-width))
                 0.8)
      :x-velocity 0
      :y-velocity 0
      :x 19
      :y 6
      :player? true
      :id :mei
      :health 5
      :recovering 0
      :direction :down)))


(defn create-sprites []
  "Parses mei.png spritesheet for all frames using the width/height of a tile to split it."
  (let [sheet (g2d/texture "mei.png")
        tiles (g2d/texture! sheet :split
                            (-> const/sprite-map :mei :tile-width)
                            (-> const/sprite-map :mei :tile-height))
        mei-images (vec (for [row (range (-> const/sprite-map :mei :tile-rows))]
                          (vec (for [col (range (-> const/sprite-map :mei :tile-cols))]
                                 (g2d/texture (aget tiles row col))))))]
    (create mei-images)))


(defn move
  "Moves the playable character. Retrieves the delta-time from the screen object
  and can potentially check for the velocity of multiple entities. delta-time
  refers to the time passed since last frame update (last on-render call). Also, retrieves
  receives an entity (usually the player) where we use it's initial x/y values."
  [{:keys [delta-time]} entities {:keys [x y] :as entity}]
  (let [[x-velocity y-velocity] (get-velocity entities entity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change) (not= 0 y-change))
      (assoc entity
             :x-velocity (entity-utils/decelerate x-velocity)
             :y-velocity (entity-utils/decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change))
      entity)))


(defn animate
  "Animates a character on the screen. Receives all the possible animation states."
  [screen {:keys [x-velocity y-velocity stand-right stand-left
                  stand-up stand-down jump-right jump-left
                  run-right run-left run-up run-down] :as entity}]
  (let [direction (get-direction entity)]
    (merge entity
           (cond
             ; animations for running up/down
             (not= y-velocity 0)
             (cond
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


(defn prevent-move
  "Prevents character from moving when touching walls."
  [screen entities {:keys [x y x-change y-change] :as entity}]
  (let [old-x (- x x-change)
        old-y (- y y-change)
        entity-x (assoc entity :y old-y)
        entity-y (assoc entity :x old-x)]
    (merge entity
           (when (entity-utils/get-touching-tile screen entity-x "walls")
             {:x-velocity 0 :x-change 0 :x old-x})
           (when-let [tile (entity-utils/get-touching-tile screen entity-y "walls")]
             {:y-velocity 0 :y-change 0 :y old-y})
           (when (and (or (not= 0 x-change) (not= 0 y-change))
                      (entity-utils/near-entities? entities entity 2))
             {:x-velocity 0
              :y-velocity 0
              :x-change 0
              :y-change 0
              :x (- x x-change)
              :y (- y y-change)}))))


(defn hit-spike
  "Makes player hit items in the environment that cause damage"
  [screen {:keys [x y health] :as player}]
  (if (not (= :home (:current-map screen))) ; <- make this more reusable
    player
    (if (and (= (:recovering player) 0) (entity-utils/get-touching-tile screen player "spikes"))
      ; TODO: v .. add "recovering" state which makes invulnerable and diff animation for one second. and translate fluidly.
      ; TODO: create a damage-character function that takes care of the rest.. so that we may reuse for mobs as well
      ; check direction the player was moving in, and push the opposite direction
      (assoc player :health (dec health) :x (- x 2) :y (- y 2) :recovering 20) ; change quantity 20
      player)))


(defn- enter-map [tmx-file screen new-map-key]
  (let [renderer (play/orthogonal-tiled-map tmx-file (/ 1 const/pixels-per-tile))]
    (when const/DEBUG_ON (println "Entering map: " new-map-key))
    (play/update! screen :timeline [] :camera (play/orthographic)
                  :renderer renderer :current-map new-map-key)))


(defn use-exit?
  "Makes player exit one \"map\" and enter into another"
  [screen {:keys [x y health] :as player}]
  (if (entity-utils/get-touching-tile screen player "exits")
    (do
      (when const/DEBUG_ON (println "Exiting map:" (:current-map screen)))
      ; TODO: clean this doublecase / sending play/screen! signal mess
      (case (:current-map screen)
        :house (enter-map "mei-home.tmx" screen :home)
        :home (enter-map "house1.tmx" screen :house))
      (play/screen! screen :on-resize)
      (case (:current-map screen)
        :house (assoc player :x 19 :y 6)
        :home (assoc player :x 5 :y 2.5)))
    player))


;;;;;;;; more mei properties for later

;;       :jump-right (util/texture-coords mei-textures [3 16])
;;       :jump-left (g2d/texture (util/texture-coords mei-textures [3 16]) :flip true false)

;;       :walk-right (g2d/animation util/duration
;;                                  (util/texture-action-coords mei-textures 1 [0 3])
;;                                  :set-play-mode (g2d/play-mode :loop-pingpong))
;;       :walk-left (g2d/animation util/duration
;;                                 (util/texture-action-coords mei-textures 3 [0 3])
;;                                 :set-play-mode (g2d/play-mode :loop-pingpong))
