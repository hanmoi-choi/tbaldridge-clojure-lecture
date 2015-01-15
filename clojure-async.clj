(ns tbaldridge-clojure-lecture.clojure-async
  (:require [clojure.core.async :as a :refer [>!! <!! >! <!]]))

;; Episode
;; < input channel as argument
;; > output channel as argument
(let [ch (a/chan 2)]
  (>!! ch 43)
  (<!! (pipeline< [4 inc
                   1 dec
                   2 dec
                   3 str]
                  ch)))

(defn to-proc< [in]
  (let [out (a/chan 1)]
    (a/pipe in out)
    out))

(defn pipeline<
  "docstring"
  [desc c]
  (let [p (partition 2 desc)]
    (reduce
      (fn [prev-c [n f]]
        (-> (for [_ (range n)]
              (->
                (a/map< f prev-c)
                to-proc<))
            (a/merge)))
      c
      p))
  )
