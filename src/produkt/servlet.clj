(ns produkt.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:require [compojure.route :as route])
  (:use ring.util.servlet
	[produkt.routes :only [app]]))

(defservice app)