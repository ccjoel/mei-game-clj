(ns mei.entities.player-test
  (:require [clojure.test :refer :all]
            [mei.entities.player :refer :all]))

(deftest get-direction-test
  (testing "Returns a new direction given x and y velocities and previous directions"
    ; no direction returns default
    (is (= :left (get-direction {:x-velocity 0 :y-velocity 0 :direction :left})))
    ; after having pressed one direction/button at a time
    (is (= :up (get-direction {:x-velocity 0 :y-velocity 3 :direction :left})))
    (is (= :down (get-direction {:x-velocity 0 :y-velocity -3 :direction :left})))
    (is (= :right (get-direction {:x-velocity 3 :y-velocity 0 :direction :left})))
    (is (= :left (get-direction {:x-velocity -3 :y-velocity 0 :direction :up})))
    ; when pressing two keys, up/down (y) take priority over left/right (x)
    (is (= :up (get-direction {:x-velocity 3 :y-velocity 3 :direction :up})))))


(deftest move-test
  (testing "Moves to a different location"
     (is (= {:x 13, :y 19, :x-velocity 2.4000000000000004, :y-velocity 3.2, :x-change 3, :y-change 4}
            (with-redefs [get-velocity (fn [entities {:keys [player?] :as entity}] [3 4])]
              (move {:delta-time 1} [] {:x 10 :y 15 }))))))
