(ns mei.screens.utils
  (:require [play-clj.core :as play]
            [mei.constants :as const]))

(defn update-screen!
  "Updates screen / camera to follow player when moving around. Side effect function, no unit tests."
  [screen entities & screen-dimensions]
  (doseq [{:keys [x y height player?]} entities] ; doseq for side effects, for to return values
    (when player?
      ; TODO: remove magic numbers by substracting tiles to total tiles to pan camera.
      (let [new-x (if (and (> x 3.5) (< x 41)) x (.x (play/position screen)))
            new-y (if (and (> y 3.1) (< y 40)) y (.y (play/position screen)))] ; TODO: clean getting position screen?
        (play/position! screen new-x new-y))))
  entities)

(defn update-height
  "[Side effect] Sets new screen height."
  [screen update-fn]
  (let [new-height (update-fn (play/height screen))]
    (when const/DEBUG_ON (println "New height: " new-height))
    (play/height! screen new-height)))
