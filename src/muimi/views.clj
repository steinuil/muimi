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
                ["/lounge/"      "lounge"]]
               [["/readme"      "readme"]
                ["https://github.com/steinuil/muimi" "source"]]]]
    [:header (interpose " " (map header-section links))]))

(defn lounge-post [post]
  [:div [:div (:name post) " " (:date post) " No." (:id post)]
   [:div [:p (:body post)]]])

(defn lounge-thread [thread]
  (let [op (first (:posts thread))
        posts (rest (:posts thread))]
    [:article
     [:div.op [:div (:subject thread) " " (:name op) " " (:date op) " No." (:id thread)]
      [:div [:p (:body op)]]]
     (if-not (empty? posts)
       [:div.posts (map lounge-post posts)])]))

(defn page [text]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (hiccup/html [:body header [:main text]])})
