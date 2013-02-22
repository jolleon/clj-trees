(ns trees.handler
  (:use compojure.core)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.core :refer :all]
            [clojure.java.io]
  ))


;; reading file

(def all_json (parse-stream (clojure.java.io/reader "all_the_trees.json")))

(def trees (all_json "data"))

;; column access

; read-string throws NPE for missing (nil) fields
(def saferead #(if (not (nil? %)) (read-string %)))

; read a number field
(def col #(saferead (%2 %1)))

(def id (partial col 8))
(def species #(% 10))
(def address #(% 11))
(def date #(% 17))
(def size #(% 19))
(def lat (partial col 23))
(def lng (partial col 24))

;; species list

(def species-with-counts (frequencies (map species trees)))

(def species-with-counts-ordered (reverse (sort-by val species-with-counts)))

;; trees

(defn lat-lng-id [tree]
    (if (lat tree) ; verifying that the lat is not nil
        [(lat tree) (lng tree) (id tree)]
    )
)

(defn filter-species [s]
    (filter #(= s (species %)) trees)
)

(defn trees-for-species [s]
    (filter (comp not nil?)
        (map lat-lng-id (filter-species s))
    )
)


; app

(defroutes app-routes
  (GET "/species" [] (generate-string species-with-counts-ordered))
  (GET "/trees/:s" [s] (generate-string (apply concat (map trees-for-species (clojure.string/split s #"&")))))
  (GET "/tree/:i" [i] "coming soon")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))


(defn -main [port]
  (run-jetty app {:port (Integer. port)}))
