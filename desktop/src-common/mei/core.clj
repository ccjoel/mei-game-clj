(ns mei.core
  (:require [play-clj.core :as play :refer :all]
            [play-clj.g2d :as g2d :refer :all]   ;funcs for 2D games
            [play-clj.ui :as ui]  ;ui code (labels.. etc)
            [mei.core.desktop-launcher :as launch]
            [clojure.edn :as edn]
            [play-clj.repl :refer [e e! s s!]]
            ))

;; (def mei
;;     (ui/label "Hello world!" (color :white))
;; (try
;;   (texture "Clojure_logo.gif")
;;   (catch NullPointerException e (println (str e)))
;;   )
;;   )

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (texture "mei.png")
    )

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))


(defgame mei-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

;;;; repl'
(launch/-main)

()
