(ns muimi.core
  (:gen-class :main true)
  (:require [org.httpkit.server :as http-kit]
            [clojure.core.match :refer [match]]
            [clojure.string :as string]
            [hiccup.core :as hiccup]))

(defn redirect
  ([url]
   {:status 308 :headers {"Location" url} :body ""})
  ([url t]
   (let [status (case t :permanent 308 :temporary 307 :see-other 303 302)]
     {:status status :headers {"Location" url} :body ""})))

(defn stuff [s]
  (let [header
        [:header
         "[ " [:a {:href "/"} "Home"]
         " / " [:a {:href "/newsroom"} "newsroom"]
         " / " [:a {:href "/lounge/"} "lounge"]
         " / " [:a {:href "/back-issues"} "back issues"]
         " / " [:a {:href "/readme"} "readme"]
         " ]"]]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (hiccup/html [:body header [:p s]])}))

(defn app [request]
  (println (-> request :headers (get "referer")))
  (let [path (-> (:uri request) (subs 1) (string/split #"/" -1))
        method (:request-method request)]

    (match [method path]
      [:get [""]]
      (stuff "The front page. Should look like a point n click game eventually?")

      [:get ["readme"]]
      (stuff "Rules and stuff.")

      [:get ["back-issues"]]
      (stuff "Archive for the newsroom.")

      [:get ["newsroom"]]
      (stuff "Thread of the month.")

      [:post ["newsroom"]]
      (redirect "/newsroom" :see-other)

      [:get ["lounge" ""]]
      (stuff "More classic random board.")

      [:post ["lounge" ""]]
      (redirect "/lounge/" :see-other)

      [:get ["lounge"]]
      (redirect "/lounge/")

      [:get ["lounge" "all"]]
      (stuff "All threads, textboard-style.")

      [:get ["lounge" thread-id]]
      (stuff (str "All replies in " thread-id ".")) ; 429 too many requests

      [:post ["lounge" thread-id]]
      (redirect (str "/lounge/" thread-id) :see-other)

      [:post _] {:status 405 :headers {} :body ""}
      [:delete _]  {:status 405 :headers {} :body ""}
      [:put _]  {:status 405 :headers {} :body ""}

      :else (stuff (str [method path] ": not found"))
      )))

(defn -main []
  (http-kit/run-server app {:port 8080}))
