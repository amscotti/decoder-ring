(ns decoder-ring.ring
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [clojure.string :as str]))

(def letters
  ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z" " "])

(def instruction-regex #"^(L|R)(\d*)?$")

;; specs for instruction string
(s/def ::instruction-string
  (s/with-gen (s/and string? #(re-matches instruction-regex %))
    #(gen/fmap (partial apply str)
               (gen/tuple (gen/elements ["L" "R"]) (gen/choose 0 26)))))

(s/def ::instructions-string
  (s/with-gen
    (s/and string? #(re-matches #"^((L|R)(\d*)?(;)?)+$" %))
    #(gen/fmap (partial str/join ";")
               (gen/not-empty (gen/list (s/gen ::instruction-string))))))

(s/def ::letter
  (s/with-gen (s/and string? 
                     #(= (count %) 1) 
                     #((set letters) (str/upper-case %)))
    #(gen/elements letters)))

;; specs for building up an instruction
(s/def ::direction #{:Left :Right})
(s/def ::steps
  (s/with-gen
    nat-int?
    #(gen/choose 0 26)))

(s/def ::instruction
  (s/keys :req-un [::direction ::steps]))

(defn get-letter [index]
  (letters (mod index (count letters))))

(s/fdef get-letter
  :args (s/cat :index int?)
  :ret ::letter)


(defn get-instruction [instruction-string]
  (let [[_ i c] (re-matches instruction-regex instruction-string)
        count (if (empty? c) 0 (Integer/parseInt c))]
    (condp = i
      "L" {:direction :Left :steps count}
      "R" {:direction :Right :steps count})))

(s/fdef get-instruction
  :args (s/cat :instruction-string ::instruction-string)
  :ret ::instruction)


(defn create-instruction-seq [input]
  (->> (str/split input #";")
       (map get-instruction)))

(s/fdef create-instruction-seq
  :args (s/cat :input ::instructions-string)
  :ret (s/coll-of ::instruction))



(defn decode-instructions
  ([instructions] (decode-instructions 0 instructions))
  ([starting-position instructions]
   (loop [[cur & remaining] instructions position starting-position code ""]
     (let [{direction :direction steps :steps} cur]
       (if (nil? cur)
         code
         (let [pos ((if (= direction :Left) - +) position steps)]
           (recur remaining pos (str code (get-letter pos)))))))))

(s/fdef decode-instructions
  :args (s/cat :starting-position pos-int?
               :instructions (s/coll-of ::instruction))
  :ret string?)

(defn get-position-for-letter [letter]
  (let [index (.indexOf letters (str/upper-case letter))]
    (if (= index -1) 0 index)))

(s/fdef get-position-for-letter
  :args (s/cat :letter ::letter)
  :ret nat-int?)
