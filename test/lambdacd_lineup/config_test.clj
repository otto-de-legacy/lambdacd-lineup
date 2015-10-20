(ns lambdacd-lineup.config-test
  (:require [clojure.test :refer :all]
            [lambdacd-lineup.config :refer :all]
            [clojure.tools.logging :as log]))

(deftest validate-test
  (testing "valid minimal config"
    (let [cfg {:base-url "otto.de"}]
      (is (first (validate cfg)))))
  (testing "base-url: leading slash is invalid"
    (let [cfg {:base-url "/otto.de"}]
      (is (not (first (validate cfg))))))
  (testing "base-url: trailing slash is invalid"
    (let [cfg {:base-url "otto.de/"}]
      (is (not (first (validate cfg))))))
  (testing "urls: valid vector"
    (let [cfg {:base-url "otto.de" :urls ["/sport"]}]
      (is (first (validate cfg)))))
  (testing "urls: valid slash vector"
    (let [cfg {:base-url "otto.de" :urls ["/"]}]
      (is (first (validate cfg)))))
  (testing "urls: invalid vector with empty string"
    (let [cfg {:base-url "otto.de" :urls [""]}]
      (is (not (first (validate cfg))))))
  (testing "urls: two valid strings"
    (let [cfg {:base-url "otto.de" :urls ["/sport", "/media"]}]
      (is (first (validate cfg)))))
  (testing "urls: only a empty string invalid"
    (let [cfg {:base-url "otto.de" :urls ""}]
      (is (not (first (validate cfg))))))
  (testing "urls: a list is invalid"
    (let [cfg {:base-url "otto.de" :urls '("/sport" "/media")}]
      (is (not (first (validate cfg))))))
  (testing "urls: same url twice is invalid"
    (let [cfg {:base-url "otto.de" :urls ["/sport", "/sport"]}]
      (is (not (first (validate cfg))))))
  (testing "urls: without leading slash is invalid"
    (let [cfg {:base-url "otto.de" :urls ["sport", "/media"]}]
      (is (not (first (validate cfg))))))
  (testing "urls: a trailing slash is valid"
    (let [cfg {:base-url "otto.de" :urls ["/sport/"]}]
      (is (first (validate cfg)))))
  (testing "resolutions: valid empty vector"
    (let [cfg {:base-url "otto.de" :resolutions []}]
      (is (first (validate cfg)))))
  (testing "resolutions: valid vector with two resolutions"
    (let [cfg {:base-url "otto.de" :resolutions [800, 1200]}]
      (is (first (validate cfg)))))
  (testing "resolutions: only a string is invalid"
    (let [cfg {:base-url "otto.de" :resolutions "1200"}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: vector with string is invalid"
    (let [cfg {:base-url "otto.de" :resolutions ["1200"]}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: vector with a number and a string is invalid"
    (let [cfg {:base-url "otto.de" :resolutions [800, "1200"]}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: only a number invalid"
    (let [cfg {:base-url "otto.de" :resolutions 1200}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: a list is invalid"
    (let [cfg {:base-url "otto.de" :resolutions '(1200)}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: same resolution twice is invalid"
    (let [cfg {:base-url "otto.de" :resolutions [800, 1200, 800]}]
      (is (not (first (validate cfg))))))
  (testing "browser: valid firefox"
    (let [cfg {:base-url "otto.de"
               :browser :firefox}]
      (is (first (validate cfg)))))
  (testing "browser: valid phantomjs"
    (let [cfg {:base-url "otto.de"
               :browser :phantomjs}]
      (is (first (validate cfg)))))
  (testing "browser: invalid string"
    (let [cfg {:base-url "otto.de"
               :browser "phantomjs"}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: valid value"
    (let [cfg {:base-url "otto.de"
               :async-wait 3}]
      (is (first (validate cfg)))))
  (testing "async-wait: invalid negative value"
    (let [cfg {:base-url "otto.de"
               :async-wait -3}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid positive float value"
    (let [cfg {:base-url "otto.de"
               :async-wait 4.3}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid string value"
    (let [cfg {:base-url "otto.de"
               :async-wait "4"}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid vector value"
    (let [cfg {:base-url "otto.de"
               :async-wait [4,5]}]
      (is (not (first (validate cfg))))))
  (testing "protocol: valid protocol http"
    (let [cfg {:base-url "otto.de"
               :protocol "http"}]
      (is (first (validate cfg)))))
  (testing "protocol: valid protocol https"
    (let [cfg {:base-url "otto.de"
               :protocol "https"}]
      (is (first (validate cfg)))))
  (testing "protocol: invalid protocol"
    (let [cfg {:base-url "otto.de/"
               :protocol "htp"}]
      (is (not (first (validate cfg)))))))