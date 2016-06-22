(ns lambdacd-lineup.core
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd-artifacts.core :as artifacts]
            [cheshire.core :as cheshire]
            [lambdacd-lineup.config :as config]
            [lambdacd-lineup.util :as util]
            [lambdacd-lineup.io :as io]
            [clojure.string :as s]
            [clojure.core.strint :as strint]
            [clj-http.client :as client]
            [lambdacd.steps.support :refer [new-printer print-to-output printed-output]]))


(defn check-status-code-for-one-url [url-with-path]
  (let [status (:status (client/get url-with-path {:throw-exceptions false :ignore-unknown-host? true}))]
    (if (not (or (nil? status) (> status 400)))
      []
      [url-with-path])))

(defn check-status-code [url paths]
  (let [urls-with-paths (map #(util/concat-url-and-path url %1) paths)]
    (reduce (fn [old url-with-path]
              (concat old (check-status-code-for-one-url url-with-path)))
            [] urls-with-paths)))

(defn take-screenshots-output [ctx printer url paths resolutions cookies]
  (print-to-output ctx printer (str "URL: " url))
  (print-to-output ctx printer (str "Paths: " paths))
  (print-to-output ctx printer (str "Resolutions: " resolutions))
  (print-to-output ctx printer (str "Cookies: " cookies))
  (print-to-output ctx printer "")
  (print-to-output ctx printer "-------------------------------------------------")
  (print-to-output ctx printer ""))

(defn invalid-status-code-output [ctx printer url paths failed-paths]
  (print-to-output ctx printer (str "URL: " url))
  (print-to-output ctx printer (str "Paths: " paths))
  (print-to-output ctx printer "")
  (print-to-output ctx printer (str "Can't connect to " (s/join ", " failed-paths)))
  (print-to-output ctx printer "-------------------------------------------------")
  (print-to-output ctx printer ""))

(defn get-domain-from-url [url]
  (let [match (re-matches #"(?:[^:/]*:)?([^.]*\.[^.]*\.|[^/]*).*" (clojure.string/reverse url))]
    (if (nil? match)
      "can-not-extract-domain"
      (clojure.string/reverse (second match)))))

(defn set-cookie-defaults [url cookie]
  {:name (get cookie "name")
   :value (get cookie "value")
   :secure (or (get cookie "secure") false)
   :path (str "/" (or (get cookie "path") ""))
   :domain (get-domain-from-url url)
   })


(defn take-screenshot [ctx home-dir script url resolutions paths dir phantomjs? async-wait cookies]
  (shell/bash
    ctx
    home-dir
    (strint/<< "ruby lineup/~{script} \"~{url}\" \"~{resolutions}\" \"~{paths}\" \"~{dir}\" \"~{phantomjs?}\" \"~{async-wait}\" '~{cookies}'"))
  )

(defn interate-urls-to-take-screenshots
  ([lineup-cfg script-name build-number env ctx home-dir printer]
   (interate-urls-to-take-screenshots (get lineup-cfg "urls") lineup-cfg script-name build-number env ctx home-dir printer :success))
  ([urls lineup-cfg script-name build-number env ctx home-dir printer status]

   (if (or (empty? urls) (= :failure status))
     {:status status}
     (let [url-configuration (val (first urls))
           env-mapping (get url-configuration "env-mapping")
           url (util/replace-env-in-url (key (first urls)) env env-mapping)
           url-for-dir (util/replace-special-chars-in-url url)
           paths (or (get url-configuration "paths") "/")
           paths-as-string (s/join "," paths)
           cookies (or (get url-configuration "cookies") [])
           cookies-with-defaults (map (partial set-cookie-defaults url) cookies)
           cookies-as-json (cheshire/generate-string cookies-with-defaults)
           resolutions (or (get url-configuration "resolutions") (get lineup-cfg "resolutions") [(str 1200)])
           resolutions-as-string (s/join "," resolutions)
           async-wait (str (or (get lineup-cfg "async-wait") 5))
           browser (or (get lineup-cfg "browser") "firefox")
           phantomjs? (= "phantomjs" browser)
           dir (str home-dir "/screenshots/" build-number "-" url-for-dir)
           invalid-paths-list (check-status-code url paths)]
       (if (empty? invalid-paths-list)
         (do
           (take-screenshots-output ctx printer url paths-as-string resolutions-as-string cookies-as-json)
           (recur (rest urls)
                  lineup-cfg
                  script-name
                  build-number
                  env
                  ctx
                  home-dir
                  printer
                  (:status (take-screenshot ctx home-dir script-name url resolutions-as-string paths-as-string dir phantomjs? async-wait cookies-as-json))))
         (do
           (invalid-status-code-output ctx printer url paths-as-string invalid-paths-list)
           {:status :failure}))))))

(defn validation-output [ctx printer validation-result]
  (print-to-output ctx printer "Configuration Validation Error")
  (print-to-output ctx printer (second validation-result)))

(defn execute-lineup-script [env printer script-name]
  (fn [_ {build-number :build-number {home-dir :home-dir lineup-cfg :lineup-cfg} :config :as ctx}]
    (let [validation-result (config/validate lineup-cfg)]
      (if (not (first validation-result))
        (do
          (validation-output ctx printer validation-result)
          {:status :failure})
        (let [lineup-folder (io/ensure-dir home-dir "lineup")]
          (io/copy-to lineup-folder script-name)
          (interate-urls-to-take-screenshots lineup-cfg
                                             script-name
                                             build-number
                                             env
                                             ctx
                                             home-dir
                                             printer))))))

(defn take-screenshots
  {:meta-step true}
  ([] (take-screenshots "www"))
  ([env]
   (let [printer (new-printer)]
     (execute-lineup-script env printer "lineup_screenshot.rb"))))

(defn compare-with-screenshots
  {:meta-step true}
  ([] (compare-with-screenshots "www"))
  ([env]
   (let [printer (new-printer)]
     (execute-lineup-script env printer "lineup_compare.rb"))))

(defn copy-files-output [ctx printer]
  (print-to-output ctx printer "Error: Can't rename files."))

(defn rename-and-publish-lineup-files [args {build-number :build-number {home-dir :home-dir lineup-cfg :lineup-cfg} :config :as ctx} env printer]
  (loop [urls (get lineup-cfg "urls")
         result {:status :success :details []}
         artifacts-list '()]
    (if (or (empty? urls) (= :failure (:status result)))
      (assoc result :details [{:label "Artifacts", :details artifacts-list}])
      (let [env-mapping (get (val (first urls)) "env-mapping")
            url (util/replace-env-in-url (key (first urls)) env env-mapping)
            url-for-dir (util/replace-special-chars-in-url url)
            dir (str home-dir "/screenshots/" build-number "-" url-for-dir)
            shell-result (shell/bash ctx dir (str "for f in * ; do mv \"$f\" \"" url-for-dir "_$f\" ; done"))]
        (if (= :failure (:status shell-result))
          (do (copy-files-output ctx printer)
              {:status :failure})
          (let [new-artifacts-list (:details (first (:details (artifacts/publish-artifacts
                                                                args
                                                                ctx
                                                                dir [#".*"]))))
                result-with-details (update-in result [:details] #(concat % artifacts-list))]
            (recur (rest urls) result-with-details (concat artifacts-list new-artifacts-list))))))))

(defn calc-detected-max-diff [json-result]
  (if (empty? json-result)
    0
    (apply max (map :difference json-result))))

(defn calc-new-status [old-result max-detected-diff max-diff]
  (let [new-status (if (<= max-detected-diff max-diff) :success :failure)
        old-status (:status old-result)
        combined-with-old-status (if (= :failure old-status) old-status new-status)]
    {:status combined-with-old-status}))

(defn analyse-output [ctx printer url max-detected-diff max-diff]
  (print-to-output ctx printer (str "URL: " url))
  (print-to-output ctx printer (str "actual max difference: " max-detected-diff " %"))
  (print-to-output ctx printer (str "target max difference: " max-diff " %"))
  (print-to-output ctx printer (str "result: " (if (<= max-detected-diff max-diff) "Success" "Failure")))
  (print-to-output ctx printer "")
  (print-to-output ctx printer "-------------------------------------------------")
  (print-to-output ctx printer ""))

(defn error-output [ctx printer url url-for-dir]
  (print-to-output ctx printer (str "URL: " url))
  (print-to-output ctx printer (str "Can't find " url-for-dir "_log.json"))
  (print-to-output ctx printer "")
  (print-to-output ctx printer "-------------------------------------------------")
  (print-to-output ctx printer ""))

(defn iterate-and-analyse-logs [{build-number :build-number step-id :step-id {home-dir :home-dir lineup-cfg :lineup-cfg} :config :as ctx} env printer]
  (loop [urls (get lineup-cfg "urls")
         result {:status :success}]
    (if (empty? urls)
      result
      (let [dir (str home-dir "/" build-number "/" (artifacts/format-step-id step-id))
            env-mapping (get (val (first urls)) "env-mapping")
            url (util/replace-env-in-url (key (first urls)) env env-mapping)
            url-for-dir (util/replace-special-chars-in-url url)
            max-diff (get (val (first urls)) "max-diff")]
        (if (io/lineup-json-exists url home-dir build-number step-id)
          (let [json-result (cheshire/parse-stream (clojure.java.io/reader (str dir "/" url-for-dir "_log.json")) true)
                max-detected-diff (calc-detected-max-diff json-result)
                new-result (calc-new-status result max-detected-diff max-diff)]
            (analyse-output ctx printer url max-detected-diff max-diff)
            (recur (rest urls) new-result))
          (do (error-output ctx printer url url-for-dir)
              (recur (rest urls) {:status :failure})))))))

(defn analyse-comparison
  {:meta-step true}
  ([]
   (analyse-comparison "www"))
  ([env]
   (fn [args ctx]
     (let [printer (new-printer)
           result (rename-and-publish-lineup-files args ctx env printer)]
       (if (= :success (:status result))
         (assoc (iterate-and-analyse-logs ctx env printer) :details (:details result))
         result)))))

