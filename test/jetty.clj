(ns jetty
  (:use ring.adapter.jetty)
  (:use produkt.routes))

(defn boot []
  (future (run-jetty #'app {:port 8080})))