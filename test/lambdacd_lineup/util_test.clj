(ns lambdacd-lineup.util-test
  (:require [clojure.test :refer :all]
            [lambdacd-lineup.util :refer :all]))

(deftest replace-env-in-url-test
  (testing "url wo env placeholder"
    (let [url "https://www.otto.de"]
      (is (= url (replace-env-in-url url "dev" nil)))))
  (testing "url with one env placeholder"
    (let [url "https://#env#.otto.de"]
      (is (= "https://dev.otto.de" (replace-env-in-url url "dev" nil)))))
  (testing "url with two env placeholder"
    (let [url "https://#env#.#env#.otto.de"]
      (is (= "https://dev.dev.otto.de" (replace-env-in-url url "dev" nil)))))
  (testing "url with wrong placeholder"
    (let [url "https://#myenv#.otto.de"]
      (is (= "https://#myenv#.otto.de" (replace-env-in-url url "dev" nil)))))
  (testing "url with wrong placeholder"
    (let [url "https://#env#.otto.de"]
      (is (= "https://www.otto.de" (replace-env-in-url url "live" {"live" "www"}))))))

(deftest replace-special-chars-in-url-test
  (testing "url with dots"
    (let [url "www.otto.de"]
      (is (= "www_otto_de" (replace-special-chars-in-url url)))))
  (testing "typical url with special chars"
    (let [url "https://www.otto.de"]
      (is (= "https_www_otto_de" (replace-special-chars-in-url url)))))
  (testing "typical url with special chars"
    (let [url "https://www.otto-otto.de"]
      (is (= "https_www_otto_otto_de" (replace-special-chars-in-url url)))))
  (testing "typical url with special chars"
    (let [url "https://www.otto.de?myparam"]
      (is (= "https_www_otto_de_myparam" (replace-special-chars-in-url url)))))
  (testing "typical url with special chars"
    (let [url "https://www.otto.de#top"]
      (is (= "https_www_otto_de_top" (replace-special-chars-in-url url)))))
  (testing "typical url with special chars"
    (let [url "https://www.otto.de%test"]
      (is (= "https_www_otto_de_test" (replace-special-chars-in-url url))))))

(deftest concat-url-and-path-test
  (testing "url + damenmode"
    (is (= "https://www.otto.de/damenmode"
           (concat-url-and-path "https://www.otto.de" "damenmode"))))
  (testing "url storefront"
    (is (= "https://www.otto.de/"
           (concat-url-and-path "https://www.otto.de" "/")))))
