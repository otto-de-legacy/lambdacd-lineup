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
    (let [cfg {:base-url "otto.de" :urls ["sport"]}]
      (is (first (validate cfg)))))
  (testing "urls: valid empty vector"
    (let [cfg {:base-url "otto.de" :urls []}]
      (is (first (validate cfg)))))
  (testing "urls: valid vector with empty string"
    (let [cfg {:base-url "otto.de" :urls [""]}]
      (is (first (validate cfg)))))
  (testing "urls: valid vector with empty string"
    (let [cfg {:base-url "otto.de" :urls ["sport", "media"]}]
      (is (first (validate cfg)))))
  (testing "urls: only a empty string invalid"
    (let [cfg {:base-url "otto.de" :urls ""}]
      (is (not (first (validate cfg))))))
  (testing "urls: a list is invalid"
    (let [cfg {:base-url "otto.de" :urls '("sport" "media")}]
      (is (not (first (validate cfg))))))
  (testing "urls: same url twice is invalid"
    (let [cfg {:base-url "otto.de" :urls ["sport", "sport"]}]
      (is (not (first (validate cfg))))))
  (testing "urls: a leading slash is invalid"
    (let [cfg {:base-url "otto.de" :urls ["sport", "/media"]}]
      (is (not (first (validate cfg))))))
  (testing "urls: a trailing slash is valid"
    (let [cfg {:base-url "otto.de" :urls ["sport/"]}]
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