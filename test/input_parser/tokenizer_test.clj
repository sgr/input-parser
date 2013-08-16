;; -*- coding: utf-8-unix -*-
(ns input-parser.tokenizer-test
  (:require [clojure.test :refer :all]
            [input-parser.tokenizer :refer :all]))

(deftest a-test
  (testing "NIL"
    (is (= [] (tokenize nil)))
    (is (= [] (tokenize "")))
    (is (= [] (tokenize " ")))
    (is (= [] (tokenize "  ")))
    (is (= [] (tokenize "　 　"))))
  (testing "one word"
    (is (= ["foo"] (tokenize "foo")))
    (is (= ["foo"] (tokenize "foo ")))
    (is (= ["foo"] (tokenize " foo"))))
  (testing "multi word"
    (is (= ["The sun"] (tokenize "\"The sun\"")))
    (is (= ["The  sun"] (tokenize "\"The  sun\"")))
    (is (= ["The sun"] (tokenize " \"The sun\""))))
  (testing "simple"
    (is (= ["foo" "bar" "baz"] (tokenize "foo bar baz"))))
  (testing "complex"
    (is (= ["foo" "bar baz"] (tokenize "foo \"bar baz\""))))
)
