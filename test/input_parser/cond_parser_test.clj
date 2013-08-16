;; -*- coding: utf-8-unix -*-
(ns input-parser.cond-parser-test
  (:require [clojure.test :refer :all]
            [input-parser.cond-parser :refer :all])
  (:import [java.text ParseException]))

(deftest a-test
  (testing "NIL LIST"
    (is (= nil (parse "")))
    (is (= nil (parse " ")))
    (is (= nil (parse "　")))
    (is (= nil (parse "  ")))
    (is (= nil (parse "　　")))
    (is (= nil (parse "　 ")))
    (is (= nil (parse "()"))))
  (testing "one word"
    (is (= "foo" (parse "foo")))
    (is (= "foo" (parse "foo ()"))))
  (testing "multi word"
    (is (= "The sun" (parse "\"The sun\"")))
    (is (= '(:or "foo bar" "baz") (parse "(or \"foo bar\" baz)")))
    (is (= '("and and") (parse "(\"and and\")"))))
  (testing "one-node list"
    (is (= '("foo") (parse "(foo)")))
    (is (= '(:and) (parse "(and)")))
    (is (= '(:or)  (parse "(or)")))
    (is (= '(:not) (parse "(not)"))))
  (testing "flat words"
    (is (= '(:and "foo" "bar" "baz") (parse "foo bar baz")))
    (is (= '("foo" "bar" "baz") (parse "(foo bar baz)"))))
  (testing "AND"
    (is (= '(:and "foo" "bar" "baz") (parse "(and foo bar baz)")))
    (is (= '(:and "and") (parse "(and and)"))))
  (testing "OR"
    (is (= '(:or "foo" "bar" "baz")  (parse "(or foo bar baz)"))))
  (testing "NOT"
    (is (= '(:not "foo") (parse "(not foo)"))))
  (testing "complex condition"
    (is (= '(:and "foo" (:or "bar" "baz") "abc") (parse "(and foo (or bar baz) abc)"))))
  (testing "spurious parenthesis"
    (is (thrown? ParseException (parse "(")))
    (is (thrown? ParseException (parse "foo)")))
    (is (thrown? ParseException (parse "(and foo bar )or baz abc)")))
    (is (thrown? ParseException (parse "(and \"foo bar)\""))))
)
