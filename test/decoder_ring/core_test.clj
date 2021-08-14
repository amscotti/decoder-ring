(ns decoder-ring.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.alpha :as s]
            [decoder-ring.core :as core]))



(deftest defaults
  (testing "Should return the give letter from the command line args"
    (let [letter "A"]
      (is (= letter (->> (s/conform ::core/args ["R19" letter])
                         (core/defaults)
                         :starting-letter)))))

  (testing "Should return A if no letter it set from the command line args"
    (let [letter "A"]
      (is (= letter (->> (s/conform ::core/args ["R19"])
                         (core/defaults)
                         :starting-letter))))))