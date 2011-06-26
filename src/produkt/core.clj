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

(defrecord Produkt [varenr navn services hardware meta])

(defrecord Service [id navn prov-system prov-id])

(defrecord Hardware [id navn logistik-system logistik-kode har-sn])

(defrecord Link [bucket key tag])

(defn riak-put [bucket rec]
  (riak/put rc bucket (:id rec)
            {:value (.getBytes (json/json-str rec))
             :content-type "application/json"}))

(defn riak-get [bucket rec]
  (let [res (riak/get rc bucket (:id rec))]
    (when res
      (json/read-json (String. (:value res))))))

(extend-type Service
  ProduktHandler
  (opret [p]
    (riak-put "services" p))
  (slet [p]
    (riak/delete rc "services" (:id p)))
  (opdater [p o])
  (hent [p]
    (let [s (riak-get "services" p)]
      (Service. (:id s) (:navn s) (:prov-system s) (:prov-id s)))))

(extend-type Hardware
  ProduktHandler
  (opret [p]
    (riak-put "hardware" p))
  (slet [p]
    (riak/delete rc "hardware" (:id p)))
  (opdater [p o])
  (hent [p]
    (let [h (riak-get "hardware" p)]
      (Hardware. (:id h) (:navn h) (:logistik-system h) (:logistik-kode h) (:har-sn h)))))

(extend-type Produkt
  ProduktHandler
  (opret [p]
    (let [links (concat (:services p) (:hardware p))
          value (dissoc p :services :hardware)]     
      (riak/put rc "produkter" (:varenr value)
            {:value (.getBytes (json/json-str value))
             :content-type "application/json"
             :links links})))
  (slet [p]
    (riak/delete rc "produkter" (:varenr p)))
  (opdater [p o])
  (hent [p]
    (let [res (riak/get rc "produkter" (:varenr p))
          links (:links res)
          services (filter #(= (:tag %) "service") links)
          hardware (filter #(= (:tag %) "hw") links)
          p (assoc (json/read-json (String. (:value res))) :services services :hardware hardware)]      
      (when res
        (Produkt. (:varenr p) (:navn p) (:services p) (:hardware p) (:meta p))))))

(defn test-p []
  (let [s (Service. "1101001" "GP" "Bier" "GP")
        h (Hardware. "123" "Boks" "IRIS" "SAMS-HD" true)
        p (Produkt. "1101111" "Underligt bundle" [(Link. "services" "1101001" "service")] [(Link. "hardware" "123" "hw")] {:sorteringsgruppe "1122" :sortering "1" :kortnavn "Und. Bu" :varetype "bu"})]
    (opret s)
    (opret h)
    (opret p)
    (hent p)))

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