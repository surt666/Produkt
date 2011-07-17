(ns produkt.routes
  (:use compojure.core
        ring.util.response
        produkt.core
        yousee-common.wrappers
        ring.commonrest
        yousee-common.web)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]            
            [clojure.contrib.json :as json]))

(defn- opret-service [body]
  (produkt.core.Service. (:id body) (:navn body) (:prov-system body) (:prov-id body)))

(defn- opret-hw [body]
  (produkt.core.Hardware. (:id body) (:navn body) (:logistik-system body) (:logistik-kode body) (:har-sn body)))

(defn- opret-produkt [body]
  (let [services (map #(produkt.core.Link. "services" % "service") (:service-ids body))
        hardware (map #(produkt.core.Link. "hardware" % "hw") (:hardware-ids body))]
    (produkt.core.Produkt. (:varenr body) (:navn body) services hardware (:meta body))))

(defn- opret-objekt [type body]
  (prn type body)
  (cond
   (= type "service") (opret-service body)
   (= type "hardware") (opret-hw body)
   (= type "produkt") (opret-produkt body)))
  
(defroutes handler
  (GET "/produkt/findalle/:bucket" [bucket]
       (json/json-str (find-alle bucket)))

  (GET "/produkt/find/:type/:id" [type id]
       (json/json-str
        (hent
         (cond
          (= type "service") (produkt.core.Service. id nil nil nil)
          (= type "hardware") (produkt.core.Hardware. id nil nil nil nil)
          (= type "produkt") (produkt.core.Produkt. id nil nil nil nil)))))

  (DELETE "/produkt/slet/:type/:id" [type id]          
          (try
            (slet
             (cond
              (= type "service") (produkt.core.Service. id nil nil nil)
              (= type "hardware") (produkt.core.Hardware. id nil nil nil nil)
              (= type "produkt") (produkt.core.Produkt. id nil nil nil nil)))
            (json-response nil "application/json" :status 200)
            (catch Exception e
              (json-response (.getMessage e) "application/json" :status 409))))
  
 
  (POST "/produkt/opret/:type" req
        (prn req)
        (let [type (get-in req [:route-params :type])
              body (parse-body (:body req))
              objekt (opret-objekt type body)]
          (prn "B" type body)
          (try
            (opret objekt)
            (json-response {:res true} "application/json" :status 201)
            (catch Exception e
              (prn "E" (.getMessage e))
              (json-response (.getMessage e) "application/json" :status 409)))))

  (route/not-found "UPS det er jo helt forkert det der !"))

(def app
  (-> (handler/site handler)
      (wrap-request-log-and-error-handling :body-str :body :status :server-port :query-string :server-name :uri :request-method :content-type :headers :json-params :params)      
      (wrap-jsonp "callback"))) 