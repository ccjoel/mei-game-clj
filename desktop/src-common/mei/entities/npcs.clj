(ns mei.entities.npcs
  (:require [play-clj.g2d :as g2d]
            [play-clj.math :refer [rectangle]]
            [mei.constants :as const]))


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
           :stand-right stand-right
           :dialog "Hello!"
           :npc? true)))
