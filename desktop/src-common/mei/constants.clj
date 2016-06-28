(ns mei.constants
  (:require [clojure.edn :as edn]))

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(def DEBUG_ON true)
