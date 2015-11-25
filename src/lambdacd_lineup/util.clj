(ns lambdacd-lineup.util
  (:require [clojure.string :as s]))

(defn replace-env-in-url [url env env-mapping]
  (let [env-mapped (get env-mapping env)]
    (if (nil? env-mapped)
      (s/replace url #"#env#" env)
      (s/replace url #"#env#" env-mapped))))

(defn replace-special-chars-in-url [url]
  (s/replace url #"[^a-zA-Z0-9]+" "_"))

(defn concat-url-and-path [url path]
  (if (= path "/")
    (str url path)
    (str url "/" path)))
