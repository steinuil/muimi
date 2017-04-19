(ns muimi.core
  (:gen-class :main true)
  (:use [muimi.views])
  (:require [org.httpkit.server :as http-kit]
            [clojure.core.match :refer [match]]
            [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]))

(def db-spec {:dbtype "h2" :dbname "./muimi"})

(defn redirect
  ([url]
   {:status 308 :headers {"Location" url} :body ""})
  ([url t]
   (let [status (case t :permanent 308 :temporary 307 :see-other 303 302)]
     {:status status :headers {"Location" url} :body ""})))

(defn app [request]
  ;(println (-> request :headers (get "referer")))
  (let [path (-> (:uri request) (subs 1) (string/split #"/" -1))
        method (:request-method request)]

    (match [method path]
      [:get [""]]
      (page "The front page. Should look like a point n click game eventually?")

      [:get ["readme"]]
      (page "Rules and stuff.")

      [:get ["back-issues"]]
      (page "Archive for the newsroom.")

      [:get ["newsroom"]]
      (page "Thread of the month.")

      [:post ["newsroom"]]
      (redirect "/newsroom" :see-other)

      [:get ["lounge" ""]]
      (page "More classic random board.")

      [:post ["lounge" ""]]
      (redirect "/lounge/" :see-other)

      [:get ["lounge"]]
      (redirect "/lounge/")

      [:get ["lounge" "all"]]
      (page "All threads, textboard-style.")

      [:get ["lounge" thread-id]]
      (page (str "All replies in " thread-id ".")) ; 429 too many requests

      [:post ["lounge" thread-id]]
      (redirect (str "/lounge/" thread-id) :see-other)

      [:get ["test"]]
      (page (jdbc/query db-spec ["SELECT ROWNUM() AS ROW, * FROM ayy WHERE lmao = 'oi' ORDER BY -1"]))

      [:get ["test" ""]]
      (page (jdbc/query db-spec ["SHOW COLUMNS FROM files"]))

      [:post _]    {:status 405 :headers {} :body ""}
      [:delete _]  {:status 405 :headers {} :body ""}
      [:put _]     {:status 405 :headers {} :body ""}

      :else (page (str [method path] ": not found")))))

(defn -main []
  (let [port 8080]
    (println (str "Serving muimi on port " port))
    (http-kit/run-server app {:port port})))


(defonce server (atom nil))

(defn reload []
  (when @server (@server))
  (use 'muimi.core :reload)
  (reset! server (-main))
  nil)
