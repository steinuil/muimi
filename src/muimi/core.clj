(ns muimi.core
  (:gen-class :main true)
  (:use [muimi.config]
        [muimi.views])
  (:require [org.httpkit.server :as http-kit]
            [clojure.core.match :refer [match]]
            [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]))

(defn redirect
  ([url]
   {:status 308 :headers {"Location" url} :body ""})
  ([url t]
   (let [status (case t :permanent 308 :temporary 307 :see-other 303 302)]
     {:status status :headers {"Location" url} :body ""})))


(defn- clob-to-string [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))


(defn- lounge-index-previews [thread-id]
  (jdbc/query db-spec
    ["(SELECT TOP 1 * FROM lounge_posts WHERE thread = ? ORDER BY id) UNION
      (SELECT TOP ? * FROM lounge_posts WHERE thread = ? ORDER BY -id) ORDER BY ID"
     thread-id post-previews thread-id]
    {:row-fn #(assoc % :body (clob-to-string (:body %)))}))


(defn lounge-index []
  (->> (jdbc/query db-spec ["SELECT * FROM lounge_threads"])
       (map #(->>
               (:id %)
               (lounge-index-previews)
               (assoc % :posts)
               (lounge-thread)))))

(defn app [request]
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
      (page (cons [:h2 "More classic random board."] (lounge-index)))

      [:post ["lounge" ""]]
      (redirect "/lounge/" :see-other)

      [:get ["lounge"]]
      (redirect "/lounge/")

      [:get ["lounge" thread-id]]
      (page (str "All replies in " thread-id ".")) ; 429 too many requests

      [:post ["lounge" thread-id]]
      (redirect (str "/lounge/" thread-id) :see-other)

      [:get ["test"]]
      (page "test")

      [:post _]    {:status 405 :headers {} :body ""}
      [:delete _]  {:status 405 :headers {} :body ""}
      [:put _]     {:status 405 :headers {} :body ""}

      :else (page (str [method path] ": not found")))))

(defn -main []
  (let [port 8080]
    (println (str "Serving muimi on port " port))
    (http-kit/run-server app {:port port})))
