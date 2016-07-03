;;;;;;;; more mei properties for later

;;       :jump-right (util/texture-coords mei-textures [3 16])
;;       :jump-left (g2d/texture (util/texture-coords mei-textures [3 16]) :flip true false)

;;       :walk-right (g2d/animation util/duration
;;                                  (util/texture-action-coords mei-textures 1 [0 3])
;;                                  :set-play-mode (g2d/play-mode :loop-pingpong))
;;       :walk-left (g2d/animation util/duration
;;                                 (util/texture-action-coords mei-textures 3 [0 3])
;;                                 :set-play-mode (g2d/play-mode :loop-pingpong))


; sample from play-clj sample apples app

;; (defn- remove-touched-apples [entities]
;;   (if-let [apples (filter #(contains? % :apple?) entities)]
;;     (let [player (some #(when (:player? %) %) entities)
;;           touched-apples (filter #(rectangle! (:hit-box player) :overlaps (:hit-box %)) apples)]
;;       (remove (set touched-apples) entities))
;;     entities))

;; (defn- move-player [entities]
;;   (->> entities
;;        (map (fn [entity]
;;               (->> entity
;;                    (update-player-position)
;;                    (update-hit-box))))
;;        (remove-touched-apples)))

;; (defn- spawn-apple []
;;   (let [x (+ 50 (rand-int 1400))
;;         y (+ 50 (rand-int 30))]
;;     (assoc (texture "apple.png") :x x, :y y, :width 50, :height 65, :apple? true)))
