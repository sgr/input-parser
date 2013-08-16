;; -*- coding: utf-8-unix -*-
(ns #^{:author "sgr"
       :doc "文字列を空白で区切る。"}
  input-parser.tokenizer
  (:require [clojure.string :as s]))

(defn space? [^Character c] (some #(= % c) '(\space \tab \　)))
(defn quote? [^Character c] (= \" c))
(defn escape? [^Character c] (= \\ c))

(declare in-word in-space in-quote)

(defn- in-word [src token dst]
  (cond (= 0 (count src)) (if (< 0 (count token)) (conj dst (s/join token)) dst)
        (space? (first src)) #(in-space (rest src) [] (conj dst (s/join token)))
        (quote? (first src)) #(in-quote (rest src) [] (conj dst (s/join token)))
        :else                #(in-word  (rest src) (conj token (first src)) dst)))

(defn- in-space [src token dst]
  (cond (= 0 (count src)) (if (< 0 (count token)) (conj dst (s/join token)) dst)
        (space? (first src)) #(in-space (rest src) [] dst)
        (quote? (first src)) #(in-quote (rest src) [] dst)
        :else                #(in-word  (rest src) [(first src)] dst)))

(defn- in-quote [src token dst]
  (cond (= 0 (count src)) (if (< 0 (count token)) (conj dst (s/join token)) dst)
        (quote? (first src))  #(in-space (rest src) [] (conj dst (s/join token)))
        (escape? (first src)) (let [rsrc (rest src)]
                                #(in-quote (rest rsrc) (conj token (first rsrc)) dst))
        :else                 #(in-quote (rest src) (conj token (first src)) dst)))

(defn tokenize [^String s] (trampoline in-space (char-array s) [] []))
