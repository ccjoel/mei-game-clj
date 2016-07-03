(ns mei.entities.utils-test
  (:require [clojure.test :refer :all]
            [mei.entities.utils :refer :all]
            [mei.constants :refer [damping deceleration h-home-tiles v-home-tiles]]))

(deftest decelerate-test
  (testing "Returns correct decelerated velocity when velocity > damping"
    (is (== 3
            (with-redefs [deceleration 0.5
                          damping 2]
              (decelerate 6)))))

  (testing "Returns 0 as velocity when updated-velocity < damping"
    (with-redefs [damping 2]
      (is (= 0 (decelerate 1))))))


(deftest near-entity?-test
  (testing "Returns true or false to entities close to it"
    (is (=
          true
          (near-entity? {:x 1 :y 1 :id :player} {:x 1 :y 1 :id :dwarf :npc? true} 1)))
    (is (=
          false
          (near-entity? {:x 1 :y 1 :id :player} {:x 3 :y 3 :id :dwarf :npc? true} 1)))
    (is (=
          true
          (near-entity? {:x 1 :y 1 :id :player} {:x 3 :y 3 :id :dwarf :npc? true} 3)))))


(deftest remove-particles-when-done-test
  (testing "removes particle when outside of map"
    (with-redefs [h-home-tiles 40 v-home-tiles 40]
      (is (=
            [{:x 1 :y 1 :particle? true} {:x 1000 :y 1000}]
            (vec
              (remove-particles-when-done [{:x 1 :y 1 :particle? true}
                                           {:x -1 :y 1 :particle? true}
                                           {:x 2 :y -1 :particle? true}
                                           {:x 100 :y 1 :particle? true}
                                           {:x 1 :y 100 :particle? true}
                                           {:x 1000 :y 1000}])))))))
