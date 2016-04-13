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

;(def amq-url )
;(def amq-url "ws://rsoskic:61614/stomp")

(def client (js/Stomp.client "ws://localhost:61614/stomp"))

(defn onconn [frame]
  (.subscribe client "/topic/test" output-message))

(defn init-stomp
  "Initializes STOMP infrastructure"
  []
  (set! (.-debug client) #(append! (by-id "debug") (html [:p %])))
  (listen! (by-id "connect")
           :click
           (fn [_]
             (.debug client "Connecting...")
             (.connect client "guest" "guest" onconn (fn [err] (.debug client err)))
             (.debug client "Connected.")
             false))
  (listen! (by-id "disconnect")
           :click
           (fn [_]
             (.disconnect client #(.debug client "Disconnected"))
             false)))

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
    (init-stomp)))

(set! (.-onload js/window) init)