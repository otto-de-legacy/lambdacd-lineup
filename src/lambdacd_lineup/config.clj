(ns lambdacd-lineup.config
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]
            [clojure.string :as s]
            [clojure.tools.logging :as log])
  (use [bouncer.validators :only [defvalidator]]))

(defvalidator without-leading-slash?
              {:default-message-format "%s must not start with a slash"}
              [s]
              (or (nil? s)
                  (= "/" s)
                  (nil? (re-find #"^/.*$" s))))

(defvalidator with-leading-slash?
              {:default-message-format "%s has to start with a slash"}
              [s]
              (or (nil? s)
                  (not (nil? (re-find #"^/.*$" s)))))

(defvalidator without-trailing-slash?
              {:default-message-format "%s must not end with a slash"}
              [s]
              (or (nil? s)
                  (nil? (re-find #"^.*/$" s))))

(defvalidator http-or-https?
              {:default-message-format "%s must be http or https"}
              [s]
              (or (nil? s)
                  (not (nil? (re-find #"^https?.*$" (key s))))))

(defvalidator is-vector?
              {:default-message-format "%s must be a vector"}
              [s]
              (or (nil? s)
                  (instance? clojure.lang.APersistentVector s)))

(defvalidator not-empty?
              {:default-message-format "%s must not be empty"}
              [s]
              (not (empty? s)))

(defvalidator is-map?
              {:default-message-format "%s must be a map"}
              [s]
              (or (nil? s)
                  (instance? clojure.lang.APersistentMap s)))

(defvalidator no-duplicate-entries-in-map?
              {:default-message-format "%s must not have duplicate entries"}
              [s]
              (or (nil? s)
                  (= (count (distinct (keys s))) (count (keys s)))))

(defvalidator no-duplicate-entries-in-vector?
              {:default-message-format "%s must not have duplicate entries"}
              [s]
              (or (nil? s)
                  (= (count (distinct s)) (count s))))

(defvalidator is-positive?
              {:default-message-format "%s must be a positve number"}
              [s]
              (or (nil? s)
                  (and (number? s) (or (pos? s) (= 0.0 (float s))))))

(defvalidator is-integer?
              {:default-message-format "%s must be a integer"}
              [s]
              (or (nil? s)
                  (and (number? s) (integer? s))))

(defvalidator is-float?
              {:default-message-format "%s must be a integer"}
              [s]
              (or (nil? s)
                  (and (number? s) (or (integer? s) (float? s)))))

(defvalidator is-firefox-or-phantomjs?
              {:default-message-format "%s must be :firefox or :phantomjs"}
              [s]
              (or (nil? s)
                  (contains? #{:firefox :phantomjs} s)))

(defn validate [cfg]
  (let [val-result (first (b/validate cfg
                                      "urls" [is-map?
                                              no-duplicate-entries-in-map?
                                              [v/every http-or-https?]
                                              [v/every #(without-trailing-slash? (key %))]
                                              [v/every  #(b/valid? (val %) "env-mapping" [is-map? no-duplicate-entries-in-map?])]
                                              [v/every  #(b/valid? (val %) "max-diff" [v/required is-positive? is-float?])]
                                              [v/every  #(b/valid? (val %) "paths" [[v/every without-leading-slash?]])]
                                              [v/every  #(b/valid? (val %) "paths" [[v/every not-empty?]])]
                                              [v/every  #(b/valid? (val %) "paths" [v/required not-empty?])]]
                                      "resolutions" [is-vector? no-duplicate-entries-in-vector? [v/every v/number]]
                                      "browser" [is-firefox-or-phantomjs?]
                                      "async-wait" [is-positive? is-integer?]))]
    (if (nil? val-result)
      [true nil]
      [false val-result])))