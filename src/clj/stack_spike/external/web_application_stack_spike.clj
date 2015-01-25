(ns stack-spike.external.web-application-stack-spike
  (:require [stack-spike.external.web-application :as web-application]
            [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [liberator.dev :refer [wrap-trace]]
            [bidi.ring :as b]
            [stack-spike.interface.routes :refer [routes]]
            [stack-spike.interface.resources :refer [resources]]
            [stack-spike.external.url :refer [local-root-url]]
            [clojure.string :as str]
            [io.clojure.liberator-transit]
            [ring.middleware.transit :refer [wrap-transit-body]]
            [stack-spike.utility.debug :refer [dbg]]))

(defn form-method [request]
  (if-let [fm (or (get-in request [:headers "x-http-method-override"])
                  (get-in request [:form-params "_method"]))]
    (assoc request :request-method (keyword (str/lower-case fm)))
    request))

(defn wrap-form-method [handler]
  (fn [request]
    (handler (form-method request))))

(defrecord WebApplicationStackSpike [db host-name port handler]

  component/Lifecycle

  (start [component]
    (assoc component
      :handler (web-application/make-handler component)))

  (stop [component]
    (dissoc component :handler))

  web-application/WebApplication

  (make-handler [this]
    (->  (b/make-handler
          stack-spike.interface.routes/routes
          (fn [r] (get (stack-spike.interface.resources/resources
                        (:db this)
                        (str (local-root-url port)))
                       r
                       r)))
      wrap-anti-forgery
      wrap-form-method
      wrap-params
      (wrap-transit-body {:keywords? true} )
      wrap-session
      (wrap-trace :header :ui)
      wrap-stacktrace-web)))


(defn new-web-application-stack-spike [host-name port]
  (map->WebApplicationStackSpike {:host-name host-name, :port port}))
