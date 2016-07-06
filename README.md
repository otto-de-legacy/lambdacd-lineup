# LambdaCD-Lineup

With LambdaCD-Lineup you can take and compare screenshots of multiple urls in your pipeline. This is very helpful if you have a webservice and you want to ensure that changes do not affect the GUI.

[![Clojars Project](http://clojars.org/lambdacd-lineup/latest-version.svg)](http://clojars.org/lambdacd-lineup)

Check out [Lineup](https://github.com/otto-de/lineup) to get more information about this project.
LambdaCD-Lineup is just a wrapper to integrate this tool in LambdaCD.

## Requirements
* Ruby
* PhantomJS or headless Firefox
* [Lineup](https://github.com/otto-de/lineup) >= 0.7.0

Both must exist in any directory in your $PATH.
## Usage

```clojure
(defn -main [& args]
  (let [home-dir (util/create-temp-dir)
          artifacts-path-context "/artifacts"
          lineup-cfg (lambdacd-lineup.io/load-config-file "lineup.json")
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
The LambdaCD-Artifacts plugin needs this path to serve your screenshots.

### Define your LambdaCD-Lineup config
```javascript
{
  "urls": {
    "https://#env#.otto.de": {
      "paths": [
        "/",
        "multimedia"
      ],
      "max-diff": 2,
      "env-mapping": {
        "live": "www"
      },
      "cookies": [
        {
          "name": "enableAwesomeFeature1",
          "value": "true"
        },
        {
          "name": "enableAwesomeFeature2",
          "value": "false"
        }
      ],
      "local-storage" : {
        "key1": "value1",
        "key2": "value2"
      }
      "resolutions": [
        800,
        1200
      ]
    },
    "http://#env#.ottogroup.com" : {
      "paths": [
        "de"
      ],
      "max-diff": 2
    }
  },
  "browser": "firefox",
  "async-wait": 10,
  "resolutions": [
    600,
    800
  ]
}
```
* urls: Map of urls on configs (no default)
  * Key: URL without path (no defualt). A placehoder #env# can be used to inject a environment.
  * Value:
    * paths: Path to subsites. URL + "/" + paths = otto.de/sport, otto.de/media (default: "/")
    * max-diff: max difference between two screenshots (before and after)
    * env-mapping: This mapping will replace the environment (argument of take-screenshots and analyse-comparison) with the corresponding value in this map.
                   Example: You call analyse-comparison live but in one url you need the string wwww
    * cookies: Set cookie for this url
      * name: Name of the cookie
      * value: Value of the cookie
      * path: Path of this cookie (optional, default: "/")
      * secure: Boolean. Only send cookie if you use https (optional, default: false)
      * resolutions: Width of the screenshots for this URL (overrules outer resolutions in config)
    * local-storage: Set local-storage key/value pairs in browser
* resolutions: Width of the screenshots (default: 1200)
* browser: "firefox" or "phantomjs" (default: :firefox)
* async-wait: Time to wait in seconds between rendering the page and taking the screenshots. Useful to load resources (fonts,...) asynchronously (default: 5)

### LambdaCD config
Add your lineup config and the artifacts-path-context to the LambdaCD config.

```
config {:lineup-cfg               lineup-cfg
        :artifacts-path-context   artifacts-path-context
        [...]}
```
### Add LambdaCD-Lineup steps to your pipeline
```clojure
(def pipeline-def
  `(
     [...]
     (lineup/take-screenshots "develop")
     deploy-my-app
     (lineup/compare-with-screenshots "develop")
     (lineup/analyse-comparison "develop")
     [...]
     ))
```

### take-screenshots
Execute this step before you deploy your changes.

Parameters:
* environment: develop, live, ... (default: www)

  Will be used in your base-url instead of placeholder #env#
### compare-with-screenshots
Execute this step after you deploy your changes.

This step compares the current version of your website with the screenshots taken in the step 'take-screenshots'.

Parameters:
* environment: develop, live, ... (default: www)

  Will be used in your base-url instead of placeholder #env#
### analyse-comparison
Execute this step after you compare the versions.

Parameter:
* environment: develop, live, ... (default: www)

  Will be used in your base-url instead of placeholder #env#

You can see the screenshots if you click on this step:

![ScreenShot](/doc/images/lambdacd-lineup-1.png)

![ScreenShot](/doc/images/lambdacd-lineup-2.png)

## License

Copyright © 2015 OTTO (GmbH & Co. KG)

Distributed under MIT License
