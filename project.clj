(defproject muimi "0.0.0"
  :description "An imageboard"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-core "1.5.1"]
                 [http-kit "2.2.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]]

  :main muimi.core
  :profiles {:uberjar {:aot :all}})
