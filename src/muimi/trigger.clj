(ns muimi.trigger
  (:import [java.sql ResultSet]
           [java.sql Connection])
  (:gen-class
    :extends org.h2.tools.TriggerAdapter))

(defn -fire-Connection-ResultSet-ResultSet
  [this conn oldRow newRow]
  (println
    (str "deleting file: "
         (apply str (map #(format "%x" %) (.getBytes oldRow "hash"))) "\n"
         "with name:     " (.getString oldRow "name") "\n"
         "and mime:      " (.getString oldRow "mime"))))
