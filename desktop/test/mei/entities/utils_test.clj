(ns mei.entities.utils-test
  (:require [clojure.test :refer :all]
            [mei.entities.utils :refer :all]
            [mei.constants :refer [damping deceleration]]))

(deftest decelerate-test
  (testing "Returns correct decelerated velocity when velocity > damping"
    (is (== 3
            (with-redefs [deceleration 0.5
                          damping 2]
              (decelerate 6)))))

  (testing "Returns 0 as velocity when updated-velocity < damping"
    (with-redefs [damping 2]
      (is (= 0 (decelerate 1))))))
