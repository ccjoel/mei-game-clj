(ns mei.entities
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [mei.util :as util]))

(defn create
  [all-mei-textures]
  (assoc stand
    ; assoc more!
         :stand-right stand
         :stand-left (texture stand :flip true false)
         :jump-right jump
         :jump-left (texture jump :flip true false)
         :walk-right (animation util/duration
                                walk
                                :set-play-mode (play-mode :loop-pingpong))
         :walk-left (animation util/duration
                               (map #(texture % :flip true false) walk)
                               :set-play-mode (play-mode :loop-pingpong))
         :width 1
         :height (/ 26 18)
         :x-velocity 0
         :y-velocity 0
         :x 20
         :y 10
         :me? true
         :can-jump? false
         :direction :right))
