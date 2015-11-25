(ns lambdacd-lineup.io
  (:require [clojure.java.io :as io]
            [cheshire.core :as cheshire]
            [lambdacd-lineup.util :as util]
            [lambdacd-artifacts.core :as artifacts])
  (:import (java.io File)))

(defn ensure-dir [parent dirname]
  (let [d (io/file parent dirname)]
    (.mkdirs d)
    d))

(defn copy-to [dir resource-name]
  (let [res-file-name (last (.split resource-name "/"))
        out-filename (str dir "/" res-file-name)]
    (with-open [in (io/input-stream (io/resource resource-name))]
      (io/copy in (io/file out-filename)))))

(defn- browser-val-to-keyword [m]
  (update-in m ["browser"] keyword))

(defn load-config-file [resource-name]
  (browser-val-to-keyword (cheshire/parse-string (slurp (io/resource resource-name)))))

(defn lineup-json-exists [url home-dir build-number step-id]
  (let [dir (str home-dir "/" build-number "/" (artifacts/format-step-id step-id))
        url-for-dir (util/replace-special-chars-in-url url)]
    (.exists
      (io/as-file (str dir "/" url-for-dir "_log.json")))))
