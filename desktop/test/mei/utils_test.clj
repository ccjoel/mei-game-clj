(ns mei.utils-test
  (:require [clojure.test :refer :all]
            [mei.utils :refer :all]))

(def mocked-vector
  (vec (map vec (take 17 (partition 8 (iterate inc 0))))))
; This generated mocked-vector looks like:
(comment [[0 1 2 3 4 5 6 7]
          [8 9 10 11 12 13 14 15]
          [16 17 18 19 20 21 22 23]
          [24 25 26 27 28 29 30 31]
          [32 33 34 35 36 37 38 39]
          [40 41 42 43 44 45 46 47]
          [48 49 50 51 52 53 54 55]
          [56 57 58 59 60 61 62 63]
          [64 65 66 67 68 69 70 71]
          [72 73 74 75 76 77 78 79]
          [80 81 82 83 84 85 86 87]
          [88 89 90 91 92 93 94 95]
          [96 97 98 99 100 101 102 103]
          [104 105 106 107 108 109 110 111]
          [112 113 114 115 116 117 118 119]
          [120 121 122 123 124 125 126 127]
          [128 129 130 131 132 133 134 135]])

(deftest texture-coords-test
  (testing "retrieves correct number by coordinates from vector"
    (is (= 100 (texture-coords mocked-vector [12 4])))))

(deftest texture-action-coords-test
  (testing "retrieves exact range of items from vector"
    (is (=
          '(19 20 21)
          (texture-action-coords mocked-vector 2 [3 6])))))

;its random.. just wanted to test the output.. could use regex
;; (deftest generate-uuid-test
;;   (testing "generates a uuid"
;;     (is (=
;;           "45656-546546546-54654654-6"
;;           (generate-uuid)
;;           ))))
