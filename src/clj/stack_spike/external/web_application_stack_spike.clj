(ns stack-spike.external.web-application-stack-spike
  (:require [stack-spike.external.web-application :as web-application]
            [com.stuartsierra.component :as component]
            [ring.middleware.stacktrace :refer [wrap-stacktrace-web]]
            [clj-stacktrace.repl :as st]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [bidi.ring :as b]
            [stack-spike.shared.routes :refer [routes]]
            [stack-spike.interface.resources :refer [resources]]
            [stack-spike.external.url :refer [local-root-url]]
            [clojure.string :as str]
            [ring.middleware.transit :refer [wrap-transit-body wrap-transit-response]]
            [clojure.tools.logging :as log]
            [stack-spike.utility.debug :refer [dbg]]))

(defn form-method [request]
  (if-let [fm (or (get-in request [:headers "x-http-method-override"])
                  (get-in request [:form-params "_method"]))]
    (assoc request :request-method (keyword (str/lower-case fm)))
    request))

(defn wrap-form-method [handler]
  (fn [request]
    (handler (form-method request))))

(defn wrap-log-exceptions
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e
        (log/error (str e "\n" (with-out-str (st/pst e))))
        (throw e)))))



(defn wrap-inject-database
  [handler db]
  (fn [request]
    (handler (assoc request :db db))))

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
          stack-spike.shared.routes/routes
          #(get stack-spike.interface.resources/resources % %))
         (wrap-inject-database (:db this))
         wrap-anti-forgery
         wrap-params
         (wrap-transit-body {:keywords? true} )
         wrap-session
         wrap-log-exceptions
         wrap-stacktrace-web)))


(defn new-web-application-stack-spike [host-name port]
  (map->WebApplicationStackSpike {:host-name host-name, :port port}))
