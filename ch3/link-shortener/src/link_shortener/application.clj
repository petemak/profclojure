(ns link-shortener.application
  (:require [link-shortener.routes :as routes]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults secure-api-defaults]]))


;; Retunrs the master handler.
;; The master handler contains all routes that the service will provide
;; There are four configurations included with the middleware
;;
;; Uses wrap-defaults middleware to set up standard/default Ring middleware based
;; on a supplied configuration in this case api-defaults.
;;
;; There are four configurations included with the middleware:
;;
;; 1) api-defaults - add support for urlencoded parameters. Can be replaced with
;;                   secure-api-defaults for SSL.
;; 2) site-defaults - add support for parameters, cookies, sessions, static resources,
;;                    file uploads, and a bunch of browser-specific security headers e.g.
;;                    antiforgery tokens and cross-site scripting protection .
;; 3) secure-api-defaults - see next.
;; 4) secure-site-defaults - force SSL and various headers and flags are sent to prevent
;;                        the browser sending sensitive information over insecure channels.
(def main-handler
  (wrap-defaults routes/app-routes api-defaults))
