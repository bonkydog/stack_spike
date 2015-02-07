(ns stack-spike.interface.html.home
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [stack-spike.interface.html.isomorphic :refer [render-fn]]
            [stack-spike.shared.routes :refer [resolve]]))


(defn home []
  (html
   [:html
    [:head]
    [:body
     [:h1 "Hello!"]
     [:p "HTML interface is no longer supported."]
     [:p "Om interface is here:"
      [:a {:href "/ships"} "OM"]]]]))




(defn om [url ships]
  (let [state-edn (prn-str {:page (resolve url)
                            :ships ships})]
    (html
     [:html
      [:head
       [:meta {:id "csrf-token" :name "csrf-token" :content *anti-forgery-token*}]]
      [:body
       [:div#root ((render-fn) state-edn)]
       [:script {:type "text/javascript" :src "/js/main.js"}]
       [:script#app-state {:type "application/edn"} state-edn]
       [:script {:type "text/javascript"} "stack_spike.om_app.init('root', 'app-state')"]]])))
