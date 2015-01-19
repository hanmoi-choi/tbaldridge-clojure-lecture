(ns async.ep2
  (:require [clojure.core.async :as a :refer [>!! <!! go >! <!]]
            [clojure.core.async.impl.protocols :as impl]))

;; Handle sequence or channel as input value
;; a/to-chan
;;  "Creates and returns a channel which contains the contents of coll,
;closing when exhausted."
(let [ch (a/chan 10)]
  (>!! ch 43)
  (<!! (a/into [] (a/take 20 (pipeline< [4 inc
                                         1 dec
                                         2 (fn [x] (a/to-chan (range x)))
                                         10 pause-rnd
                                         3 str]
                                        ch)))))

(defn pause-rnd
  ""
  [x]
  (go (<! (a/timeout (rand-int 1000)))
      x))

(rand-int 1000)
(defn to-proc< [in]
  (let [out (a/chan 1)]
    (a/pipe in out)
    out))

; a/onto-chan
; Puts the contents of coll into the supplied channel.
;  Returns a channel which will close after the items are copied.

(defn pipe-ext
  ""
  [in out]
  (a/go (loop []
          (when-some [val (<! in)]
                     (>! out val)
                     (recur)))))

(defn map-ext
  ""
  [in f out]
  (a/go (loop []
          (when-some [val (<! in)]                          ;when (<! in) is not nil, which is channel is closed
                     (let [val (f val)]
                       (cond
                         (or (seq? val)
                             (vector? val)) (do (<! (a/onto-chan out val)) ;; (<! xxx) is wait for ending of xxx
                                                (recur))
                         (extends? impl/ReadPort (class val)) (do (<! (pipe-ext val out))
                                                                  (recur))
                         :else (do (>! out val)
                                   (recur))))))))

(defn pipeline<
  "docstring"
  [desc c]
  (let [p (partition 2 desc)]
    (reduce
      (fn [prev-c [n f]]
        (let [out-c (a/chan n)]
          (dotimes [_ n]
            (map-ext prev-c f out-c))
          out-c))
      c
      p)))
