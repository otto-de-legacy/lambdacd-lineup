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
                  (nil? (re-find #"^/.*$" s))))

(defvalidator without-trailing-slash?
              {:default-message-format "%s must not end with a slash"}
              [s]
              (or (nil? s)
                  (nil? (re-find #"^.*/$" s))))

(defvalidator http-or-https?
              {:default-message-format "%s must be http or https"}
              [s]
              (or (nil? s)
                  (not (nil? (re-find #"^https?$" s)))))

(defvalidator is-vector?
              {:default-message-format "%s must be a vector"}
              [s]
              (or (nil? s)
                  (instance? clojure.lang.APersistentVector s)))

(defvalidator no-duplicate-entries?
              {:default-message-format "%s must not have duplicate entries"}
              [s]
              (or (nil? s)
                  (= (count (distinct s)) (count s))))

(defn validate [cfg]
  (let [val-result (first (b/validate cfg
                                      :base-url [v/required without-leading-slash? without-trailing-slash?]
                                      :urls [is-vector? no-duplicate-entries? [v/every without-leading-slash?]]
                                      :resolutions [is-vector? no-duplicate-entries? [v/every v/number]]
                                      :protocol http-or-https?))]
    (if (nil? val-result)
      [true nil]
      [false val-result])))