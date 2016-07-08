(defproject lambdacd-lineup "0.5.2"
  :description "LambdaCD library to integrate Lineup"
  :url "https://github.com/otto-de/lambdacd-lineup"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:name "git"
        :url "https://github.com/otto-de/lambdacd-lineup"}
  :test-paths ["test", "example"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [lambdacd "0.6.1"]
                 [lambdacd-artifacts "0.1.0"]
                 [bouncer "0.3.3"]
                 [org.clojure/core.incubator "0.1.3"]
                 [clj-http "2.0.0"]
                 [clj-http-fake "1.0.1"]]
  :profiles {:uberjar {:aot :all}}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :main example-pipeline.pipeline)
