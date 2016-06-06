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
  (testing "env-mapping: valid two mappings"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" {"live" "www"
                                                                                            "develop" "dev"}}}}]
      (is (first (validate cfg)))))
  (testing "env-mapping: invalid vector"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" ["live" "www"]}}}]
      (is (not (first (validate cfg))))))
  (testing "env-mapping: key must be a string"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" {:live "www"}}}}]
      (is (not (first (validate cfg))))))
  (testing "env-mapping: key must not be empty"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" {"" "www"}}}}]
      (is (not (first (validate cfg))))))
  (testing "env-mapping: value must be a string"
    (let [cfg {"urls" {"http://otto.de" {"paths" ["/", "sport"] "max-diff" 5 "env-mapping" {"live" :www}}}}]
      (is (not (first (validate cfg))))))

  (testing "cookies: valid map"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name"   "myCookieName"
                                                      "value"  "myCookieValue"
                                                      "secure" true}]}}}]
      (is (first (validate cfg)))))
  (testing "cookies: cookie must be a vector"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  {"name"  "myCookieName"
                                                     "value" "myCookieValue"}}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: all cookies must be verified"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name"  "myCookieName"
                                                      "value" "myCookieValue"}
                                                     {"name" "myCookieName"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: two valid cookies"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name"  "myCookieName"
                                                      "value" "myCookieValue"}
                                                     {"name"  "myCookieName"
                                                      "value" "myCookieValue"}]}}}]
      (is (first (validate cfg)))))
  (testing "cookies: name is required"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"value" "myCookieValue"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: value is required"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: name must be a string"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" 123 "value" "myCookieValue"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: value must be a string"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" 123}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: name must not be empty"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "" "value" "myCookieValue"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: value must not be empty"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" ""}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: path must be a string"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "path" 123}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: valid root path"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "path" "/"}]}}}]
      (is (first (validate cfg)))))
  (testing "cookies: valid path"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "path" "subpath"}]}}}]
      (is (first (validate cfg)))))
  (testing "cookies: invalid path. it starts with a slash"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "path" "/subpath"}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: path must not be empty"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "path" ""}]}}}]
      (is (not (first (validate cfg))))))
  (testing "cookies: secure must be a boolean"
    (let [cfg {"urls" {"http://otto.de" {"paths"    ["/"]
                                         "max-diff" 5
                                         "cookies"  [{"name" "myCookieName" "value" "myCookieValue" "secure" "true"}]}}}]
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