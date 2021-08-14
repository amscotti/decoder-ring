(ns decoder-ring.ring-test
  (:require [clojure.test :refer [deftest testing is are]]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop :include-macros true]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [decoder-ring.ring :as ring]
            [clojure.string :as str]))

(comment
  (-> (stest/enumerate-namespace 'decoder-ring.ring) stest/check))


(def get-letter-prop
  (prop/for-all [r (gen/choose 0 26)]
                (let [output (ring/get-letter r)]
                  (and (string? output)
                       #(= (count %) 1)
                       (= r (ring/get-position-for-letter output))))))

(deftest get-letter
  (testing "Should test the properties"
    (is (:pass? (tc/quick-check 1000 get-letter-prop))))
  (testing "Should return a letter for a index"
    (are [input output] (= output (ring/get-letter input))
      -2 "Z"
      -1 " "
      0 "A"
      1 "B"
      2 "C"
      24 "Y"
      25 "Z"
      26 " "
      27 "A")))

(deftest get-instruction
  (testing "Should return instruction"
    (are [input output] (= output (ring/get-instruction input))
      "L" {:direction :Left, :steps 0}
      "L0" {:direction :Left, :steps 0}
      "L1" {:direction :Left, :steps 1}
      "L10" {:direction :Left, :steps 10}
      "R" {:direction :Right, :steps 0}
      "R0" {:direction :Right, :steps 0}
      "R1" {:direction :Right, :steps 1}
      "R10" {:direction :Right, :steps 10})))


(def create-instruction-seq-prop
  (prop/for-all [r (s/gen ::ring/instructions-string)]
                (let [instruction-seq (ring/create-instruction-seq r)]
                  (= (count (str/split r #";")) (count instruction-seq)))))

(deftest create-instruction-seq
  (testing "Should test the properties"
    (is (:pass? (tc/quick-check 1000 create-instruction-seq-prop))))
  (testing "Should return list of instructions"
    (is (= '({:direction :Right, :steps 1} {:direction :Left, :steps 10})
           (ring/create-instruction-seq "R1;L10")))))

(def decode-instructions-prop
  (prop/for-all [r (gen/list (s/gen ::ring/instruction))]
                (string? (ring/decode-instructions r))))

(deftest decode-instructions
  (testing "Should test the properties"
    (is (:pass? (tc/quick-check 1000 decode-instructions-prop))))
  (testing "Should return a String based on list instructions without starting position"
    (are [input output] (= output (ring/decode-instructions input))
      '({:direction :Right, :steps 5} {:direction :Right, :steps 14} {:direction :Right, :steps 3}) "FTW"
      '({:direction :Right, :steps 22} {:direction :Left, :steps 3} {:direction :Left, :steps 14}) "WTF"))
  (testing "Should return a String based on list instructions with starting position"
    (are [input output] (= output (ring/decode-instructions 2 input))
      '({:direction :Right, :steps 3} {:direction :Right, :steps 14} {:direction :Right, :steps 3}) "FTW"
      '({:direction :Right, :steps 20} {:direction :Left, :steps 3} {:direction :Left, :steps 14}) "WTF")))


(def get-position-for-letter-prop
  (prop/for-all [r (s/gen ::ring/letter)]
                (let [output (ring/get-position-for-letter r)]
                  (and (nat-int? output)
                       (= r (ring/get-letter output))))))

(deftest get-position-for-letter
  (testing "Should test the properties"
    (is (:pass? (tc/quick-check 1000 get-position-for-letter-prop))))
  (testing "Should return the position for a letter"
    (are [input output] (= output (ring/get-position-for-letter input))
      "A" 0
      "B" 1
      "C" 2))
  (testing "Should return 0 if input not found"
    (is (= 0 (ring/get-position-for-letter "NOT FOUND")))))

(def full-string-decoding-prop
  (prop/for-all [r (s/gen ::ring/instructions-string)]
                (let [instruction-seq (ring/create-instruction-seq r)
                      decoded-string (ring/decode-instructions instruction-seq)]
                  (and (string? decoded-string)
                       (= (count decoded-string) (count instruction-seq))))))

(deftest full-string-decoding
  (testing "Should test the properties"
    (is (:pass? (tc/quick-check 1000 full-string-decoding-prop))))
  (testing "Testing decoding of full inputs strings"
    (are [input output] (= output (ring/decode-instructions (ring/create-instruction-seq input)))
      "R7;L3;R7;R0;R3;R12;L4;L8;R3;L6;L8"
      "HELLO WORLD"

      "R1;R3;L5;L8;R2;L3;R14;L5;L7;L5;R12;R4;R14;L9;R5;L3;L11;L2;L10;R6;L3;R9;L12;R7;R6;R11;R8;L11;R5;L9"
      "BE SURE TO DRINK YOUR OVALTINE")))
