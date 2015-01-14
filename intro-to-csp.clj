
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
