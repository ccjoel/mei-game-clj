(ns mei.constants
  (:require [clojure.edn :as edn]))

(def ^:const sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(def ^:const DEBUG_ON (if (= (System/getenv "mei_debug") "true") true false))

(def ^:const vertical-tiles 43)
;; (def ^:const horizontal-tiles 43)
