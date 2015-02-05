(ns stack-spike.interface.html.home
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))


(defn home []
  (html
   [:html
    [:head]
    [:body
     [:h1 "Hello!"]
     [:p "HTML interface is no longer supported."]
     [:p "Om interface is here:"
      [:a {:href "/ships"} "OM"]]]]))




(defn om [ships]
  (let [state-edn (prn-str {:page {:handler :ships
                                   :params {}}
                            :ships ships})]
    (html
     [:html
      [:head
       [:meta {:id "csrf-token" :name "csrf-token" :content *anti-forgery-token*}]]
      [:body
       [:div#root]
       [:script {:type "text/javascript" :src "/js/main.js"}]
       [:script#app-state {:type "application/edn"} state-edn]
       [:script {:type "text/javascript"} "stack_spike.om_app.init('root', 'app-state')"]]])))
