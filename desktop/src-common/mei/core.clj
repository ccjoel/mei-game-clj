(ns mei.core
  (:require [play-clj.core :as play :refer :all]
            [play-clj.g2d :as g2d :refer :all]   ;funcs for 2D games
            [play-clj.ui :as ui]  ;ui code (labels.. etc)
            [mei.core.desktop-launcher :as launch]
            [clojure.edn :as edn]
            [play-clj.repl :refer [e e! s s!]]
            [mei.entities :as me]
            ))

;; (def mei
;;     (ui/label "Hello world!" (color :white))
;; (try
;;   (texture "Clojure_logo.gif")
;;   (catch NullPointerException e (println (str e)))
;;   )
;;   )

(def sprite-map
  (edn/read-string (slurp (clojure.java.io/resource "sprite-map.edn"))))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defn get-cols [entity-name]
  (range (-> sprite-map entity-name :tile-cols)))

(defn get-rows [entity-name]
  (range (-> sprite-map entity-name :tile-rows)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))

;;     (texture "mei.png")

;;     aget

    (let [sheet (texture "mei.png")
          tiles (texture! sheet :split (-> sprite-map :mei :tile-width) (-> sprite-map :mei :tile-height))
          mei-images (for [col (get-cols :mei)]
                          (for [row (get-rows :mei)]
                          (texture (aget tiles row col))))]
      (apply me/create mei-images))

    )

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))


(defgame mei-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! mei-game blank-screen)))))


;;;; repl'ing
(launch/-main)

(on-gl (set-screen! mei-game main-screen))
