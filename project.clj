(defproject muimi "0.0.0"
  :description "An imageboard"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/java.jdbc "0.7.0-alpha3"]
                 [hiccup "1.0.5"]
                 [ring/ring-core "1.5.1"]
                 [http-kit "2.2.0"]
                 [com.h2database/h2 "1.4.194"]]
  :aot [muimi.trigger]
  :main muimi.core
  :profiles {:uberjar {:aot :all}})
