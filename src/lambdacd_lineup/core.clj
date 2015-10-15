(ns lambdacd-lineup.core
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd-artifacts.core :as artifacts]
            [cheshire.core :as cheshire]
            [lambdacd-lineup.config :as config]
            [lambdacd.util :as util]
            [lambdacd-lineup.io :as io]
            [clojure.string :as s]))

(defn execute-lineup-script [sub-domain script-name]
  (fn [_ {build-number :build-number {home-dir :home-dir lineup-cfg :lineup-cfg} :config :as ctx}]
    (let [lineup-folder (io/ensure-dir home-dir "lineup")
          protocol (or (:protocol lineup-cfg) "https")
          base-url (:base-url lineup-cfg)
          urls (or (:urls lineup-cfg) "")
          urls-as-string (s/join "," urls)
          resolutions (or (:resolutions lineup-cfg) 1200)
          resolutions-as-string (s/join "," resolutions)
          browser (or (:browser lineup-cfg) :firefox)
          browser-as-bool (= :phantomjs browser)
          async-wait (or (:async-wait lineup-cfg) 5)
          async-wait-as-string (str async-wait)
          dir (str "screenshots/" build-number "-" sub-domain)
          cfg-validation-result (config/validate lineup-cfg)]
      (if (not (first cfg-validation-result))
        (do
          (shell/bash ctx home-dir (str "echo \"Configuration Validation Error\" && echo \"" (second cfg-validation-result) "\""))
          {:status :failure})
        (do
          (io/copy-to lineup-folder script-name)
          (shell/bash
            ctx
            home-dir
            (s/join " " [(str "ruby lineup/" script-name)
                         protocol
                         sub-domain
                         base-url
                         resolutions-as-string
                         urls-as-string
                         dir
                         browser-as-bool
                         async-wait-as-string])))))))

(defn take-screenshots
  ([] (take-screenshots "www"))
  ([sub-domain]
   (execute-lineup-script sub-domain "lineup_screenshot.rb")))

(defn compare-with-screenshots
  ([] (compare-with-screenshots "www"))
  ([sub-domain]
   (execute-lineup-script sub-domain "lineup_compare.rb")))

(defn analyse-comparison
  ([threshold]
   (analyse-comparison threshold "www"))
  ([threshold sub-domain]
   (fn [args {build-number :build-number {home-dir :home-dir} :config :as ctx}]
     (let [json-path (str home-dir "/screenshots/" build-number "-" sub-domain "/log.json")
           json-result (cheshire/parse-stream (clojure.java.io/reader json-path) true)
           artifacts-list (:details (artifacts/publish-artifacts
                                      args
                                      ctx
                                      (str home-dir "/screenshots/" build-number "-" sub-domain) [#".*"]))
           max-diff (if (empty? json-result)
                      0
                      (apply max (map :difference json-result)))]

       (shell/bash
         ctx
         home-dir
         (str "echo \"Max Diff: " max-diff "\""))
       (if (< max-diff threshold)
         {:status :success :details artifacts-list}
         {:status :failure :details artifacts-list})))))