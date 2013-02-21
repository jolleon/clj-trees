(ns trees.handler
  (:use compojure.core)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(require '[cheshire.core :refer :all])
(require 'clojure.java.io)


;; reading file

(def all_json (parse-stream (clojure.java.io/reader "all_the_trees.json")))

(def trees (all_json "data"))

;; species list

(def species #(% 10))

(def all-species (set (map species trees)))

(defn filter-species [s]
    (filter #(= s (species %)) trees)
)

(def species-with-counts (map #(vector % (count (filter-species %))) all-species)) 

(def species-with-counts-ordered (reverse (sort-by second species-with-counts)))

;; trees

(def lat-lng-id #(vector (read-string (% 23)) (read-string (% 24)) (% 0)))

(defn trees-for-species [s]
    (map lat-lng-id (filter-species s))
)

; app

(defroutes app-routes
  (GET "/species" [] (generate-string species-with-counts-ordered))
  (GET "/trees/:s" [s] (generate-string (trees-for-species s)))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))


(defn -main [port]
  (run-jetty app {:port (Integer. port)}))
