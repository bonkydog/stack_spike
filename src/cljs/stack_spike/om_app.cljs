(ns stack-spike.om-app
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [cljs-http.client :as http]
            [bidi.bidi :as bidi]
            [clojure.browser.repl]
            [weasel.repl :as weasel]
            [figwheel.client :as figwheel :include-macros true]
            [stack-spike.tools :refer [log]]))

(enable-console-print!)


(def routes
  ["/om" {"/ships" :ships
          ["/ships/" :id] :ship}])

(defn current-path []
  (.-pathname (js/URL. (.-href (.-location js/document)))))

(defn resolve [url-or-path]
  (let [path (try (-> url-or-path
                      js/URL.
                      .-pathname)
                  (catch js/Object e
                    url-or-path))]
    (bidi/match-route routes path)))

(def app-state
  (atom {:page (resolve (current-path))
         :ships nil}))


(defn navigate [owner event]
  (.preventDefault event)
  (put! (om/get-shared owner :nav-chan) (.-href (.-target event)))
  nil)


(defn ship-row [id-ship-pair owner]
  (om/component
   (let [ship (last id-ship-pair)]
     (dom/tr {:id (str "ship-" (:id ship)) :class "ship"}
             (dom/td {:id "id"}
                     (dom/a {:href (str "/om/ships/" (:db/id ship))
                                :class "edit"
                             :on-click (partial navigate owner)}
                            (:db/id ship)))
             (dom/td {:class "id"}
                     (:ship/name ship))))))

(defn handle-change [e owner {:keys [name]}]
  (.preventDefault e)
  (println "handling change!")
  (om/set-state! owner :ship/name (.. e -target -value)))




(defn ship [ship owner]
  (reify
    om/IInitState
    (init-state [_]
      {:db/id (:db/id ship) :ship/name (:ship/name ship)})
    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit (fn [e]
                                                           (.preventDefault e)
                                                           (put! (om/get-shared owner :update-chan) state )
                                                           nil
                                                           )}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :autofocus "autofocus"
                            :on-change #(handle-change % owner state)})
                (dom/input {:type "submit" :value "Update Ship"})))))


(defn new-ship [ship owner]
  (reify
    om/IInitState
    (init-state [_]
      {:db/id (:db/id ship) :ship/name (:ship/name ship)})
    om/IRenderState
    (render-state [this state]
      (dom/form {:class "ship" :method "POST" :on-submit (fn [e]
                                                           (.preventDefault e)
                                                           (put! (om/get-shared owner :create-chan) state )
                                                           nil)}
                (dom/label {:for "name"} "Name")
                (dom/input {:id "name" :type "text" :name "name" :value (:ship/name state) :autofocus "autofocus"
                            :on-change #(handle-change % owner state)})
                (dom/input {:type "submit" :value "Create Ship"})))))

(defn ships [ships owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (dom/table {:class "ships"}
                          (apply dom/tbody nil
                                 (om/build-all ship-row ships)))
               (dom/a {:class "new-ship" :href "/om/ships/new" :on-click (partial navigate owner) } "New Ship")))
    ))

(defn loading []
  (om/component
   (dom/h1 "Loading...")))

(defn not-found []
  (om/component
   (dom/h1 nil "Not found.")))

(defn render-page [app]
  (if (nil? (:ships app))
    (om/build loading nil)
    (condp = (get-in app [:page :handler])
      :ships (om/build ships (get app :ships))
      :ship (let [ship-id (get-in app [:page :route-params :id])]
              (prn ship-id)
              (if (= "new" ship-id)
                (om/build new-ship {:db/id nil :ship/name ""})
                (om/build ship
                          (get (get app :ships)
                               (long ship-id)))))
      (om/build not-found nil))))

(defn fetch-ships [app]
  (go (let [response (<! (http/get "/ships" {:headers {"Accept" "application/transit+json;verbose"}}))]
        (om/update! app :ships (:body response)))))


(defn page [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil (render-page app)))
    om/IWillMount
    (will-mount [this]
      (fetch-ships app))))

(defn set-page [path]
  (om/update! (om/root-cursor app-state) :page (resolve path)))

(defn goto [url]
  (.pushState js/history {} nil url )
  (set-page url))

(def csrf-token
  (.-content (.getElementById js/document "csrf-token")))

(defn de-namespace-keys [m]
  (apply hash-map (mapcat (fn [[k v]] [(-> k name keyword) v]) m)))

(defn main []
  (let [nav-chan (chan)
        update-chan (chan)
        create-chan (chan)]
    (go-loop []
      (alt! nav-chan ([url]
                      (log "nav-chan: " url)
                      (goto url)
                      (log "nav-chan complete!"))
            update-chan ([ship-update]
                         (http/post (str "/ships/" (:db/id ship-update))
                                    {:transit-params (de-namespace-keys  ship-update)
                                     :headers {"Accept" "application/transit+json;verbose"
                                               "X-HTTP-Method-Override" "PUT"
                                               "X-CSRF-Token" csrf-token}})
                         (om/update! (om/root-cursor app-state) [:ships (:db/id ship-update)] ship-update)
                         (goto "/om/ships"))
            create-chan ([ship-creation]
                         (let [response (<! (http/post "/ships"
                                                       {:transit-params (de-namespace-keys  ship-creation)
                                                        :headers {"Accept" "application/transit+json;verbose"
                                                                  "X-CSRF-Token" csrf-token}}))]
                           (prn (:body response))
                           (om/update! (om/root-cursor app-state) [:ships (:db/id (:body response))] (:body response))
                           (goto "/om/ships"))))

      (recur))

    (set! (.-onpopstate js/window) (fn [e]
                                     (log e)
                                     (log "pop!")
                                     (set-page (current-path))))
    (om/root page
             app-state
             {:target (. js/document (getElementById "root"))
              :shared {:nav-chan nav-chan
                       :update-chan update-chan
                       :create-chan create-chan}})))
