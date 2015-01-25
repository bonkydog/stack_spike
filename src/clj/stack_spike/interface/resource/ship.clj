(ns stack-spike.interface.resource.ship
  (:require [liberator.core :refer [defresource]]
            [liberator.representation :refer [ring-response]]
            [stack-spike.interface.presenter.ship :as ship-presenter]
            [stack-spike.use-case.list-ships :refer [list-ships]]
            [stack-spike.use-case.view-ship :refer [view-ship new-ship create-ship update-ship delete-ship]]
            [stack-spike.external.database :refer [entity-gateway]]
            [stack-spike.interface.routes :as r]
            [stack-spike.utility.debug :refer [dbg]]
            [clojure.tools.logging :refer [debug]]))


(defn content [request]
  (condp = (:content-type request)
    "application/transit+json" (:body request)
    "application/x-www-form-urlencoded" (:params request)))

(defresource ship [db root-url]
  :available-media-types ["text/html", "application/transit+json"]
  :allowed-methods [:get :put :delete]
  :exists? (fn [ctx]
             (assoc ctx ::ship
                    (let [ship-id (get-in ctx [:request :params :id])]
                      (if (= ship-id "new")
                        (new-ship)
                        (view-ship
                         (entity-gateway db)
                         (Long/parseLong ship-id))))))
  :put! (fn [ctx]
          (let [params (content (:request ctx))]
            {::id (update-ship (entity-gateway db) params)}))
  :delete! (fn [ctx]
             (let [id (get-in ctx [:request :params :id])]
               (delete-ship (entity-gateway db) id)) )
  :new? false
  :respond-with-entity? (fn [ctx] (= :get (get-in ctx [:request :request-method])))
  :handle-ok (fn [ctx]
               (ship-presenter/present-ship-show (::ship ctx)))
  :handle-no-content (fn [ctx]
                       (ring-response {:status  303
                                       :headers {"Location" (r/url-for root-url :ships)}
                                       :body    ""})))

(defresource ship-list [db root-url]
  :available-media-types ["text/html", "application/transit+json"]
  :allowed-methods [:get :post]
  :handle-ok (fn [req]
               (let [media-type (get-in req [:representation :media-type])]
                 (condp = media-type
                   "text/html" (ship-presenter/present-ship-index
                                (list-ships (entity-gateway db)))
                   "application/transit+json" (list-ships (entity-gateway db))
                   (str "bad media type:" media-type))))

  :post! (fn [ctx]
           (let [params (content (:request ctx))]
             {::id (create-ship (entity-gateway db) params)}))

  :post-redirect? (fn [ctx] {:location (r/url-for root-url :ships)}))
