(ns mei.entities.npcs
  (:require [play-clj.g2d :as g2d]
            [play-clj.math :refer [rectangle]]
            [mei.constants :as const]))


(defn create-npc
  "Receives a vector of columns, and returns a texture with all
  other texture/animation states as conj'ed properties."
  [down up stand-right walk-right] ; npcs
  (let [down-flip (g2d/texture down :flip true false)
        up-flip (g2d/texture up :flip true false)
        stand-flip (g2d/texture stand-right :flip true false)
        walk-flip (g2d/texture walk-right :flip true false)]
    (assoc down
      :width 2
      :height 2
      :x-velocity 0
      :y-velocity 0
      :min-distance 2
      :health 6
      :direction :down
;;       :hurt-sound (sound "monsterhurt.wav")
      :down (g2d/animation const/duration [down down-flip])
      :up (g2d/animation const/duration [up up-flip])
      :right (g2d/animation const/duration [stand-right walk-right])
      :left (g2d/animation const/duration [stand-flip walk-flip])
      :min-distance 10
      :health 5
      :damage 2
      :attack-time 0)))


(defn create-dwarf []
  (let [sheet (g2d/texture "dwarf.png")
        tiles (g2d/texture! sheet :split 20.75 21)
        dwarf-images (for [col [0 1 2 3]]
                       (g2d/texture (aget tiles 0 col)))
        stand-right (first dwarf-images)]
    (assoc stand-right
      :width 2
      :height 2
      :x 15
      :y 4.3
      :health 1000
      :stand-right stand-right
      :dialog "Hello!"
;;       :hit-box (rectangle 15 4 2 2)
      :npc? true)))

;; (defn randomize-locations
;;   [screen entities {:keys [width height] :as entity}]
;;   (->> (for [tile-x (range 0 (- u/map-width width))
;;              tile-y (range 0 (- u/map-height height))]
;;          {:x tile-x :y tile-y})
;;        shuffle
;;        (drop-while #(u/invalid-location? screen entities (merge entity %)))
;;        first
;;        (merge entity {:id (count entities)})
;;        (conj entities)))
