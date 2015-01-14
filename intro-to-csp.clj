
(ns tbaldridge-clojure-lecture.intro-to-csp
  (:require [clojure.core.async :as a]))
(comment
  (-> (create-frame)
      (add-body)
      (map add-tire (make-tire 5))
      (add-engine)
      (ship-car))
  )

(def c 5)
(-> c (+ 3) (/ 2) (- 1))
(use 'clojure.walk)
(macroexpand-all '(-> c (+ 3) (/ 2) (- 1))) ; => (- (/ (+ c 3) 2) 1)

(comment
                    (create-frame)               ;; each form is processes
                        |
                        V
                    (add-body)
                        |    (make-tire 5)
                        |         |  ;; faster , buffer size = 6000
                        V         V
(combine-tires-with-car) (combine-tires-with-car) ;; Use multiple processes for better performance
                        |   ;; slower
                        V
                    (add-engine)
                        |   ;; slower
                        V
                    (ship-car)
)

(def c (a/chan))

(a/put! c "hello" (fn [_] (println "Done Sending")))

(a/take! c (fn [val] (println val)))
