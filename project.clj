(defproject produkt "1.0.0-SNAPSHOT"
  :description "RIAK baseret produkt katalog"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojars.ossareh/clj-riak "0.1.0-SNAPSHOT"]
                 [compojure "0.6.4"]
                 [ring/ring-core "0.3.10" :exclusions [javax.servlet/servlet-api]]
                 [ring/ring-servlet "0.3.10" :exclusions [javax.servlet/servlet-api]]
                 [ring-json-params "0.1.3"]
                 [yousee-common "1.0.27"]
                 [ring-common "1.1.1"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]]
  :dev-dependencies [[ring/ring-jetty-adapter "0.3.10"]
                     [ring/ring-devel "0.3.10"]])