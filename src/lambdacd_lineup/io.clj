(ns lambdacd-lineup.io
  (:require [clojure.java.io :as io])
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
