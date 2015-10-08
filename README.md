# lambdacd-lineup

With Lambdacd-Lineup you can take and compare screenshots of multiple urls in your pipeline. This is very helpful if you have a webservice and you want to ensure that changes do not affect the GUI.

Check out [Lineup](https://github.com/otto-de/lineup) to get more information about this project.
Lambdacd-Lineup is just a wrapper to integrate this tool in LambdaCD.

## Requirements
* Ruby
* PhantomJS or headless Firefox

Both must exist in any directory in your $PATH.
## Usage

```clojure
(defn -main [& args]
  (let [home-dir (util/create-temp-dir)
          artifacts-path-context "/artifacts"
          lineup-cfg {:base-url    "otto.de"
                      :urls        ["/sport", "/media"]
                      :resolutions [600, 800, 1200]
                      :protocol    "https"}
        config {:lineup-cfg                         lineup-cfg
                :home-dir                           home-dir
                :dont-wait-for-completion           false
                :artifacts-path-context             artifacts-path-context}
        pipeline (lambdacd.core/assemble-pipeline pipeline-def config)
        app (ui/ui-for pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    (runners/start-one-run-after-another pipeline)
    (ring-server/serve (routes
                         (context "/pipeline" [] app)
                         (context artifacts-path-context [] (artifacts/artifact-handler-for pipeline)))
                       {:open-browser? false
                        :port          8080})))
```

### Define the artifacts path
```
artifacts-path-context "/artifacts"
```
The Lambdacd-Artifacts plugin needs this path to serve your screenshots.

### Define the lambdacd-lineup config
```
lineup-cfg {:base-url    "otto.de"
            :urls        ["sport", "media"]
            :resolutions [600, 800, 1200]
            :protocol    "https"}
```
* base-url: Url of your website without subdomain (www, dev, live, ...) (no default)
* urls: Path to subsites. base-url + urls = otto.de/sport, otto.de/media (default: "")
* resolution: Width of the screenshots (default: 1200)
* protocol: https or http (default: https)

### Lambdacd config
Add your lineup config and the artifacts-path-context to the lambdacd config.

```
config {:lineup-cfg               lineup-cfg
        :artifacts-path-context   artifacts-path-context
        [...]}
```
### Add Lambdacd-Lineup steps to your pipeline
```clojure
(def pipeline-def
  `(
     [...]
     (lineup/take-screenshots "develop")
     deploy-my-app
     (lineup/compare-with-screenshots "develop")
     (lineup/analyse-comparison 10 "develop")
     [...]
     ))
```

### take-screenshots
Execute this step before you deploy your changes.

Parameters:
* sub-domain: develop, live, ... (default: www)

### compare-with-screenshots
Execute this step after you deploy your changes.

This step compares the current version of your website with the screenshots taken in the step 'take-screenshots'.

Parameters:
* sub-domain: develop, live, ... (default: www)

### analyse-comparison
Execute this step after you compare the versions.

Parameter:
* threshold: This step will fail if the difference between your versions is higher than this value (no default)
* sub-domain: develop, live, ... (default: www)

You can see the screenshots if you click on this step:

![ScreenShot](/doc/images/lambdacd-lineup-1.png)

![ScreenShot](/doc/images/lambdacd-lineup-2.png)

## License

Copyright © 2015 OTTO (GmbH & Co. KG)

Distributed under MIT License
