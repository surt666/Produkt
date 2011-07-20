(ns jetty
  (:use ring.adapter.jetty)
  (:use produkt.routes)
  (:import (org.mortbay.xml XmlConfiguration)
           (org.mortbay.jetty.webapp WebAppContext WebInfConfiguration)
           (org.mortbay.jetty.servlet Context)))

(defn init-server [server]  
  (let [c (Context. server "/" Context/SESSIONS)
        f (. c (addFilter "com.thetransactioncompany.cors.CORSFilter" "/*" 1))]
    (. f (setInitParameter "cors.allowGenericHttpRequests" "true"))
    (. f (setInitParameter "cors.allowOrigin" "*"))
    (. f (setInitParameter "cors.supportedMethods" "GET, PUT, POST, DELETE, OPTIONS"))
    (. f (setInitParameter "cors.supportedHeaders" "Content-Type, Accept"))
    (. c (addServlet "produkt.servlet" "/"))  
    (try          
      ;; (let [config (XmlConfiguration. (slurp "test/jetty.xml"))]   
      ;;   (. config (configure server)))
      (catch Exception e
        (prn "Unable to load jetty configuration")
        (. e printStackTrace)))))


(defn boot []
  (future (run-jetty #'app {:port 8080 :configurator init-server})))