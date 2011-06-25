(ns produkt.core
  (:require [clj-riak.client :as riak]
            [clojure.contrib.json :as json]))

(def rc (riak/init {:host "127.0.0.1" :port 8087}))

(defn ping-rc []
  (riak/ping rc))

(defprotocol ProduktHandler
  (opret [p])
  (slet [p])
  (opdater [p o])
  (hent [p]))

(defrecord Produkt [id navn services hardware meta])

(defrecord Service [id navn prov-system prov-id])

(defrecord Hardware [id navn logistik-system logistik-kode har-sn])

(extend-type Service
  ProduktHandler
  (opret [p]
    (riak/put rc "services" (:id p)
              {:value (.getBytes (json/json-str p))
               :content-type "application/json"}))
  (slet [p]
    (riak/delete rc "services" (:id p)))
  (opdater [p ox])
  (hent [p]
    (let [res (riak/get rc "services" (:id p))]
      (when res
        (json/read-json (String. (:value res)))))))

(extend-type Hardware
  ProduktHandler
  (opret [p]
    (riak/put rc "hardware" (:id p)
              {:value (.getBytes (json/json-str p))
               :content-type "application/json"}))
  (slet [p]
    (riak/delete rc "hardware" (:id p)))
  (opdater [p ox])
  (hent [p]
    (let [res (riak/get rc "hardware" (:id p))]
      (when res
        (json/read-json (String. (:value res)))))))

(defn test-s []
  (let [s (Service. "1101001" "GP" "Bier" "GP")]
    (opret s)
    (prn (hent s))
    (slet s)
    (hent s)))

(defn test-h []
  (let [h (Hardware. "123" "Boks" "IRIS" "SAMS-HD" true)]
    (opret h)
    (prn (hent h))
    (slet h)
    (hent h)))