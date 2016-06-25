(ns mei.util-test
  (:require [clojure.test :refer :all]
            [mei.util :refer :all]
            [clojure.pprint :as pp]))

(def mocked-vector
  (vec (map vec (take 17 (partition 8 (iterate inc 0))))))

(println "Using mocked vector: ")
(pp/pprint mocked-vector)

(deftest texture-coords-test
  (testing "retrieves correct number by coordinates from vector"
    (is (= 100 (texture-coords mocked-vector [12 4])))))

(deftest texture-action-coords-test
  (testing "retrieves exact range of items from vector"
    (is (=
          '(19 20 21)
          (texture-action-coords mocked-vector 2 [3 6])))))
