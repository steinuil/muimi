(ns muimi.views
  (:require [hiccup.core :as hiccup]))


(defn- header-section [links]
  (->> links
       (map (fn [[l n]] [:a {:href l :class "link"} n]))
       (interpose " / ")
       (#(vector :span "[ " % " ]"))))


(def header
  (let [links [[["/"            "Home"]]
               [["/newsroom"    "newsroom"]
                ["/back-issues" "back issues"]
                ["/lounge"      "lounge"]]
               [["/readme"      "readme"]
                ["https://github.com/steinuil/negoto" "source"]]]]
    [:header (interpose " " (map header-section links))]))


(defn page [text]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (hiccup/html [:body header [:p text]])})
