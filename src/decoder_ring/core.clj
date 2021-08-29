(ns decoder-ring.core
  (:require [clojure.spec.alpha :as s]
            [decoder-ring.ring :as ring])
  (:gen-class))

;; spec for command line args, which can have a instructions string and maybe a starting letter
(s/def ::args (s/or :instructions-with-starting-letter (s/cat :instructions :decoder-ring.ring/instructions-string
                                                              :starting-letter :decoder-ring.ring/letter)
                    :instructions (s/cat :instructions :decoder-ring.ring/instructions-string)))

(defmulti defaults first)

(defmethod defaults :instructions-with-starting-letter [[_ instructions]]
  instructions)

(defmethod defaults :default [[_ instructions]]
  (assoc instructions :starting-letter "A"))

(defn -main [& args]
  (let [parsed-args (s/conform ::args args)]
    (if (= ::s/invalid parsed-args)
      (do (println "Invalid Command Line Arguments")
          (println (s/explain-str ::args args)))
      (let [{instructions :instructions starting-letter :starting-letter} (defaults parsed-args)]
        (->> instructions
             (ring/create-instruction-seq)
             (ring/decode-instructions (ring/get-position-for-letter starting-letter))
             (println))))))