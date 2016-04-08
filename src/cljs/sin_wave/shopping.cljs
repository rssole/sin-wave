(ns sin-wave.shopping
  (:require [domina.core :refer [append!
                                 by-class
                                 by-id
                                 destroy!
                                 set-value!
                                 value]]
            [domina.events :refer [listen!]]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [cljs.reader :refer [read-string]]
            [hiccups.runtime])
  (:require-macros [hiccups.core :refer [html]]
                   [shoreleave.remotes.macros :as macros]))

(def amq-url "ws://rsoskic:61614/stomp")

(defn calculate []
  (let [quantity (read-string (value (by-id "quantity")))
        price (read-string (value (by-id "price")))
        tax (read-string (value (by-id "tax")))
        discount (read-string (value (by-id "discount")))]
    (remote-callback :calculate
                     [quantity price tax discount]
                     #(set-value! (by-id "total") (.toFixed % 2)))))

(defn output-message
  "Outputs message into particular placeholder"
  [msg]
  (append! (by-id "updates-holder") (html [:p msg])))

(defn init-stomp
  "Initializes STOMP infrastructure"
  [broker]
  (let [client (js/Stomp.client broker)]
    (listen! (by-id "connect")
             :click
             #(.connect client "" "" (fn [_]
                                            (.log js/console "Connected")
                                            (.subscribe client "/topic/stompy" output-message)
                                            false)))
    (listen! (by-id "disconnect")
             :click
             #(.disconnect client (fn []
                                    (.log js/console "Disconnected")
                                    false)))))

(defn init []
  (when (and js/document
             (aget js/document "getElementById"))
    (listen! (by-id "calc")
             :click
             calculate)
    (listen! (by-id "calc")
             :mouseover
             (fn []
               (by-id "shoppingForm")
               (html [:div.help "Click to calculate"])))
    (listen! (by-id "calc")
             :mouseout
             (fn []
               (destroy! (by-class "help"))))
    (init-stomp amq-url)))

(set! (.-onload js/window) init)