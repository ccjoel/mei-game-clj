(ns mei.core.desktop-launcher
  (:require [mei.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. mei-game "mei" 800 600)
  (Keyboard/enableRepeatEvents true))
