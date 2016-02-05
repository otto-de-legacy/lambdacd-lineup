(ns example-pipeline.pipeline
  (:use [lambdacd.steps.manualtrigger])
  (:require
    [ring.server.standalone :as ring-server]
    [lambdacd.ui.ui-server :as ui]
    [lambdacd.runners :as runners]
    [lambdacd.util :as util]
    [clojure.tools.logging :as log]
    [compojure.core :refer :all]
    [lambdacd-artifacts.core :as artifacts]
    [lambdacd-lineup.core :as lineup]
    [lambdacd-lineup.io :as io])
  (:gen-class))

(def pipeline-def
  `(
     wait-for-manual-trigger
     (lineup/take-screenshots "live")
     (lambdacd.steps.control-flow/either
       (lambdacd.steps.control-flow/run (lineup/compare-with-screenshots "live")
                                        (lineup/analyse-comparison "live"))
       wait-for-manual-trigger)
     ))

(defn -main [& args]
  (let [home-dir (util/create-temp-dir)
        artifacts-path-context "/artifacts"
        lineup-cfg (io/load-config-file "resources/lineup.json")
        config {:lineup-cfg               lineup-cfg
                :home-dir                 home-dir
                :dont-wait-for-completion false
                :artifacts-path-context   artifacts-path-context}
        pipeline (lambdacd.core/assemble-pipeline pipeline-def config)
        app (ui/ui-for pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    (runners/start-one-run-after-another pipeline)
    (ring-server/serve (routes
                         (context "/pipeline" [] app)
                         (context artifacts-path-context [] (artifacts/artifact-handler-for pipeline)))
                       {:open-browser? false
                        :port          8080})))

