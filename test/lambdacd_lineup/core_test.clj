(ns lambdacd-lineup.core-test
  (:require [clojure.test :refer :all]
            [lambdacd-lineup.core :refer :all]
            [clojure.tools.logging :as log]
            [clj-http.fake :as clj-http-fake]))


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

(deftest check-status-code-for-one-url-test
  (testing "valid url damenmode"
    (is (empty? (check-status-code-for-one-url "https://www.otto.de/damenmode"))))
  (testing "invalid url"
    (is (= ["https://www.otto.de/test123"]
           (check-status-code-for-one-url "https://www.otto.de/test123"))))
  (testing "invalid host"
    (is (= ["https://www.ghgsdfhksfhks.de/damenmode"]
           (check-status-code-for-one-url "https://www.ghgsdfhksfhks.de/damenmode")))))

(deftest check-status-code-test
  (testing "3 valid paths"
    (is (empty?
          (clj-http-fake/with-fake-routes
            {"https://www.otto.de/"           (fn [request] {:status 200 :headers {} :body "test"})
             "https://www.otto.de/damenmode"  (fn [request] {:status 200 :headers {} :body "test"})
             "https://www.otto.de/multimedia" (fn [request] {:status 200 :headers {} :body "test"})}
            (check-status-code "https://www.otto.de" ["/" "damenmode" "multimedia"])))))
  (testing "1 invalid path"
    (is (= ["https://www.otto.de/mytest"]
           (clj-http-fake/with-fake-routes
             {"https://www.otto.de/"           (fn [request] {:status 200 :headers {} :body "test"})
              "https://www.otto.de/mytest"     (fn [request] {:status 404 :headers {} :body "test"})
              "https://www.otto.de/multimedia" (fn [request] {:status 200 :headers {} :body "test"})}
             (check-status-code "https://www.otto.de" ["/" "mytest" "multimedia"])))))
  (testing "invalid url"
    (is (= ["https://www.ghgsdfhksfhks.de/" "https://www.ghgsdfhksfhks.de/damenmode" "https://www.ghgsdfhksfhks.de/multimedia"]
           (check-status-code "https://www.ghgsdfhksfhks.de" ["/" "damenmode" "multimedia"])))))

(deftest calc-detected-max-diff-test
  (testing "empty vector"
    (let [json []]
      (is (= 0 (calc-detected-max-diff json)))))
  (testing "five entries"
    (let [json [{:difference 1.1142448209605373},
                {:difference 48},
                {:difference 1.5},
                {:difference 9},
                {:difference 1.8237236233}]]
      (is (= 48 (calc-detected-max-diff json))))))

(deftest calc-new-status-test
  (testing "success -> failure"
    (is (= {:status :failure} (calc-new-status {:status :success} 5 2))))
  (testing "failure -> failure"
    (is (= {:status :failure} (calc-new-status {:status :failure} 1 2))))
  (testing "success -> success"
    (is (= {:status :success} (calc-new-status {:status :success} 1 2)))))