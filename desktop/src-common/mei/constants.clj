(ns mei.constants
  (:require [clojure.edn :as edn]))

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(def DEBUG_ON (if (= (System/getenv "mei_debug") "true") true false))
