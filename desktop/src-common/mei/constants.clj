(ns mei.constants
  (:require [clojure.edn :as edn]))

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(def DEBUG_ON (if (= (System/getenv "mei_debug") "true") true false))

(def v-home-tiles 43)
(def h-home-tiles 43)

(def pixels-per-tile 16)
(def duration 0.15)
(def damping 2)
(def max-velocity 7)
(def deceleration 0.8)
