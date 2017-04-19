(ns muimi.admin
  (:require [clojure.java.jdbc :as jdbc]))

(defn init [db-spec]
  (let [db-conn (jdbc/get-connection db-spec)]
    (jdbc/with-db-connection [db-conn db-spec]
      (jdbc/db-do-commands db-spec
        ["DROP TABLE IF EXISTS lounge_threads, lounge_posts, files"

         (jdbc/create-table-ddl :lounge_threads
           [[:id      :BIGINT       "IDENTITY"]
            [:updated :TIMESTAMP    "DEFAULT (CURRENT_TIMESTAMP())"]
            [:subject "VARCHAR(60)" "NOT NULL"  "CHECK (TRIM(subject) IS NOT NULL)"]
            [:locked  :BOOLEAN      "DEFAULT FALSE"]])

         (jdbc/create-table-ddl :lounge_posts
           [[:id       :BIGINT       "IDENTITY"]
            [:thread   :BIGINT       "NOT NULL"]
            [:name     "VARCHAR(40)" "NOT NULL" "CHECK (TRIM(name) IS NOT NULL)"]
            [:body     :TEXT         "DEFAULT ''"]
            [:spoiler  :BOOLEAN      "DEFAULT FALSE"]
            ["FOREIGN KEY (thread) REFERENCES lounge_threads(id)" "ON DELETE CASCADE"]])

         (jdbc/create-table-ddl :files
           [[:hash "BINARY(16)"   "PRIMARY KEY" "NOT NULL"]
            [:post :BIGINT]
            [:name "VARCHAR(255)" "DEFAULT ''"]
            [:mime "VARCHAR(255)" "NOT NULL"]
            [:width :INTEGER] [:height :INTEGER]
            ["FOREIGN KEY (post) REFERENCES lounge_posts(id)" "ON DELETE SET NULL"]])

         "DROP TRIGGER IF EXISTS delete_files"

         (str "CREATE TRIGGER delete_files "
              "AFTER DELETE ON files FOR EACH ROW CALL \"muimi.trigger\"")]))))

;(jdbc/execute! db-spec ["CREATE SEQUENCE IF NOT EXISTS lounge_posts"])
;(jdbc/insert! db-spec :ayy {:lmao "tfw"})
;"CREATE VIEW"
