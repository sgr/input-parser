input-parser
============

A Clojure library designed to parse string entered by user.

## Install

config-file is available in [Clojars.org](https://clojars.org/input-parser).
Your leiningen project.clj:

  [input-parser "0.1.0"]

## Usage

```clojure
(ns foo
  (:require [input-parser.tokenizer :as tokenizer]
            [input-parser.cond-parser :as cparser]))

(tokenizer/tokenize "foo bar baz")     ; -> ["foo" "bar" "baz"]
(tokenizer/tokenize "foo \"bar baz\"") ; -> ["foo" "bar baz"]

;; A flat words is parsed to logical AND condition list.
(cparser/parse "foo bar baz")       ; -> (:and "foo" "bar" "baz")

;; If "and", "or", "not" exists in a head of list, it is transrated to symbol.
(cparser/parse "(and foo bar baz)") ; -> (:and "foo" "bar" "baz")
(cparser/parse "(or foo bar baz)")   ; -> (:or "foo" "bar" "baz")
(cparser/parse "(not (and foo bar)") ; -> (:not (:and "foo" "bar"))
(cparser/parse "(and and)") ; -> (:and "and")
(cparser/parse "(foo bar baz)") ; -> ("foo" "bar" "baz")

;; cparser/parse don't check number of predicate's arguments
(cparser/parse "(and)") ; -> (:and)
(cparser/parse "(not foo bar) ; -> (:not "foo" "bar")
```

## License

Copyright (C) Shigeru Fujiwara All Rights Reserved.

Distributed under the Eclipse Public License, the same as Clojure.
