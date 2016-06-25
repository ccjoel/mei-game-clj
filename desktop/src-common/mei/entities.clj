(ns mei.entities
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [mei.util :as util]
            [clojure.edn :as edn]))

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(defn texture-animation [texture-array a-range]
  (apply (partial subvec texture-array) a-range))

(defn create
  [mei-textures]
  (assoc (first mei-textures)
    ; assoc more!
    :stand-up (nth mei-textures (second (-> sprite-map :mei :walk :up)))
    :stand-right (nth mei-textures (second (-> sprite-map :mei :walk :right)))
    :stand-down (nth mei-textures (second (-> sprite-map :mei :walk :down)))
    :stand-left (nth mei-textures (second (-> sprite-map :mei :walk :left)))

;;     :jump-right jump
;;     :jump-left (texture jump :flip true false)
;;     :walk-right (animation util/duration
;;                            walk
;;                            :set-play-mode (play-mode :loop-pingpong))
;;     :walk-left (animation util/duration
;;                           (map #(texture % :flip true false) walk)
;;                           :set-play-mode (play-mode :loop-pingpong))
    :width 1
    :height (/ 26 18)
    :x-velocity 0
    :y-velocity 0
    :x 20
    :y 10
    :me? true
    :can-jump? false
    :direction :down))
