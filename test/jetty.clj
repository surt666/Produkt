(ns jetty
  (:use ring.adapter.jetty)
  (:use produkt.routes)
  (:import (org.mortbay.xml XmlConfiguration)))

(defn init-server [server]
  (try    
    (let [config (XmlConfiguration. (slurp "test/jetty-web.xml"))]   
      (. config configure server))
    (catch Exception e
      (prn "Unable to load jetty configuration")
      (. e printStackTrace))))


(defn boot []
  (future (run-jetty #'app {:port 8080 :configurator init-server})))