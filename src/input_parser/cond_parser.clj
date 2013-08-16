;; -*- coding: utf-8-unix -*-
(ns #^{:author "sgr"
       :doc "条件文字列解析器"}
  input-parser.cond-parser
  (:require [clojure.string :as s])
  (:import [java.text ParseException]))

(defn space? [^Character c] (some #(= % c) '(\space \tab \　)))
(defn quote? [^Character c] (= \" c))
(defn escape? [^Character c] (= \\ c))
(defn open? [^Character c] (some #(= % c) '(\( \（)))
(defn close? [^Character c] (some #(= % c) '(\) \）)))

(declare in-word in-space in-quote)
(def ^{:dynamic true} *len* 0)

(defn- conj-top-stack [stack token]
  (if (= 0 (count token))
    stack
    (let [token (let [str-token (s/join token)]
                  (if (= 0 (count (peek stack)))
                    (condp = (s/lower-case str-token)
                      "and" :and "or" :or "not" :not str-token)
                    str-token))]
      (conj (pop stack) (conj (peek stack) token)))))

(defn- check-end [stack token]
  (let [stack (if (< 0 (count token))
                (conj-top-stack stack (s/join token))
                stack)]
    (if (= 1 (count stack))
      (let [s (peek stack)]
        (condp = (count s)
          0 nil
          1 (first s)
          (conj (apply list s) :and)))
      (throw (ParseException.
              (format "spurious open paren or missing close: %d" (count stack))
              *len*)))))

(defn- check-open [src stack token]
  #(in-space (rest src)
             (if (< 0 (count token))
               (let [f (first stack) r (rest stack)]
                 (conj r (conj f (s/join token)) []))
               (conj stack []))
             []))

(defn- check-close [src stack token]
  (cond (= 1 (count stack)) (throw (ParseException.
                                    (format "spurious close paren or missing open: %s"
                                            (pr-str (peek stack)))
                                    (- *len* (count src))))
        (< 1 (count stack)) (let [s1 (peek stack)
                                  s2 (second stack)
                                  ns2 (if (< 0 (count token))
                                        (conj s2 (apply list (conj s1 (s/join token))))
                                        (if (< 0 (count s1))
                                          (conj s2 (apply list s1))
                                          s2))
                                  new-stack (conj (-> stack pop pop) ns2)]
                              #(in-space (rest src) new-stack []))
        :else (throw (ParseException.
                      (format "spurious close paren or missing open: %d"
                              (count stack))
                      (- *len* (count src))))))

(defn- in-space [src stack token]
  (cond (= 0 (count src)) (check-end stack token)
        (open?  (first src)) (check-open src stack token)
        (close? (first src)) (check-close src stack token)
	(space? (first src)) #(in-space (rest src) stack [])
	(quote? (first src)) #(in-quote (rest src) stack [])
	:else                #(in-word  (rest src) stack [(first src)])))

(defn- in-word  [src stack token]
  (cond (= 0 (count src)) (check-end stack token)
        (open?  (first src)) (check-open src stack token)
        (close? (first src)) (check-close src (conj-top-stack stack (s/join token)) [])
	(space? (first src)) #(in-space (rest src) (conj-top-stack stack (s/join token)) [])
	(quote? (first src)) #(in-quote (rest src) (conj-top-stack stack (s/join token)) [])
	:else                #(in-word  (rest src) stack (conj token (first src)))))

(defn- in-quote [src stack token]
  (cond (= 0 (count src)) (throw (IllegalArgumentException. "missing close quotation"))
	(quote? (first src))  #(in-space (rest src) (conj-top-stack stack (s/join token)) [])
	(escape? (first src)) (let [rsrc (rest src)]
				#(in-quote (rest rsrc) stack (conj token (first rsrc))))
	:else                 #(in-quote (rest src) stack (conj token (first src)))))

(defn parse [^String s]
  (binding [*len* (.length s)]
    (trampoline in-space (char-array s) '([]) [])))
