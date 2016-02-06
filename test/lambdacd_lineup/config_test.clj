(ns lambdacd-lineup.config-test
  (:require [clojure.test :refer :all]
            [lambdacd-lineup.config :refer :all]))


(deftest validate-test
  (testing "valid minimal config"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}}}]
      (is (first (validate cfg)))))
  (testing "urls: urls is required"
    (let [cfg {"resolutions" [800, 1200]}]
      (is (not (first (validate cfg))))))
  (testing "urls: leading slash is invalid"
    (let [cfg {"urls" {"/http://otto.de" {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: leading slash is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5} "/peter.de" {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: trailing slash is invalid"
    (let [cfg {"urls" {"http://otto.de/" {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: invalid empty string"
    (let [cfg {"urls" {"" {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: invalid keyword as key"
    (let [cfg {"urls" {:mypath {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: one valid url and one invalid empty url"
    (let [cfg {"urls" {"http://www.otto.de" {"paths" ["/"] "max-diff" 5} "" {"paths" ["/"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "urls: as vector is invalid"
    (let [cfg {"urls" ["http://otto.de" {"paths" ["/"] "max-diff" 5} "http://otto.de" {"paths" ["/"] "max-diff" 5}]}]
      (is (not (first (validate cfg))))))
  (testing "urls: wrong protrocol"
    (let [cfg {"urls" ["ftp://otto.de" {"paths" ["/"] "max-diff" 5}]}]
      (is (not (first (validate cfg))))))

  (testing "paths: two valid paths - 1"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/" "sport"] "max-diff" 5}}}]
      (is (first (validate cfg)))))
  (testing "paths: two valid paths - 2"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["multimedia" "sport"] "max-diff" 5}}}]
      (is (first (validate cfg)))))
  (testing "paths: key is required"
    (let [cfg {"urls" {"http://otto.de" {"max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "paths: invalid empty path string"
    (let [cfg {"urls" {"http://otto.de" {"paths" [""] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "paths: invalid empty path"
    (let [cfg {"urls" {"http://otto.de" {"paths" [] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "paths: one valid path and one invalid empty path"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", ""] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "paths: a list is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" '("/", "") "max-diff" 5}}}]
      (is (not (first (validate cfg))))))
  (testing "paths: with leading slash is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["sport", "/media"] "max-diff" 5}}}]
      (is (not (first (validate cfg))))))

  (testing "env-mapping: valid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" {"live" "www"}}}}]
      (is (first (validate cfg)))))
  (testing "env-mapping: invalid vector"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" ["live" "www"]}}}]
      (is (not (first (validate cfg))))))

  (testing "max-diff: valid value"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}}}]
      (is (first (validate cfg)))))
  (testing "max-diff: key is required"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"]}}}]
      (is (not (first (validate cfg))))))
  (testing "max-diff: invalid negative value"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" -5}}}]
      (is (not (first (validate cfg))))))
  (testing "max-diff: valid positive float value"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5.1}}}]
      (is (first (validate cfg)))))
  (testing "max-diff: invalid string value"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" "5"}}}]
      (is (not (first (validate cfg))))))
  (testing "max-diff: invalid vector value"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" [5]}}}]
      (is (not (first (validate cfg))))))

  (testing "resolutions: valid empty vector"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" []}]
      (is (first (validate cfg)))))
  (testing "resolutions: valid vector with two resolutions"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" [800, 1200]}]
      (is (first (validate cfg)))))
  (testing "resolutions: only a string is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" "1200"}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: vector with string is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" ["1200"]}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: vector with a number and a string is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" [800, "1200"]}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: only a number invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" 1200}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: a list is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" '(1200)}]
      (is (not (first (validate cfg))))))
  (testing "resolutions: same resolution twice is invalid"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/"] "max-diff" 5}} "resolutions" [800, 1200, 800]}]
      (is (not (first (validate cfg))))))

  (testing "browser: valid firefox"
    (let [cfg {"urls"    {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "browser" "firefox"}]
      (is (first (validate cfg)))))
  (testing "browser: valid phantomjs"
    (let [cfg {"urls"    {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "browser" "phantomjs"}]
      (is (first (validate cfg)))))
  (testing "browser: invalid string"
    (let [cfg {"urls"    {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "browser" "chrome"}]
      (is (not (first (validate cfg))))))
  (testing "browser: keyword is invalid"
    (let [cfg {"urls"    {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "browser" :firefox}]
      (is (not (first (validate cfg))))))

  (testing "async-wait: valid value"
    (let [cfg {"urls"       {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "async-wait" 3}]
      (is (first (validate cfg)))))
  (testing "async-wait: invalid negative value"
    (let [cfg {"urls"       {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "async-wait" -3}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid positive float value"
    (let [cfg {"urls"       {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "async-wait" 4.3}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid string value"
    (let [cfg {"urls"       {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "async-wait" "4"}]
      (is (not (first (validate cfg))))))
  (testing "async-wait: invalid vector value"
    (let [cfg {"urls"       {"http://otto.de" {"paths" ["/"] "max-diff" 5}}
               "async-wait" [4, 5]}]
      (is (not (first (validate cfg)))))))