(defproject lambdacd-lineup "0.2.3"
  :description "LambdaCD library to integrate Lineup"
  :url "https://github.com/otto-de/lambdacd-lineup"
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :scm {:name "git"
        :url "https://github.com/otto-de/lambdacd-lineup"}
  :test-paths ["test", "example"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [lambdacd "0.5.4"]
                 [lambdacd-artifacts "0.1.0"]
                 [bouncer "0.3.3"]]
  :profiles {:uberjar {:aot :all}}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :main example-pipeline.pipeline)
