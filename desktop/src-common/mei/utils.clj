(ns mei.utils
  (:require [play-clj.core :as play]
            [mei.constants :as const]))

(defn texture-coords [textures-vector [col row]]
  (nth (nth textures-vector col) row))

(defn texture-action-coords [textures-vector col [start end]]
  (subvec (nth textures-vector col) start end))

(defn generate-uuid []
  (str (java.util.UUID/randomUUID)))
