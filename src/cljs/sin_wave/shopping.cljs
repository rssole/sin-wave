(ns sin-wave.shopping
  (:require [domina.core :refer [append!
                                 by-class
                                 by-id
                                 destroy!
                                 set-value!
                                 value]]
            [domina.events :refer [listen! prevent-default stop-propagation]]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [cljs.reader :refer [read-string]]
            [hiccups.runtime]
            [goog.json :as json])
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
  (let [body (.-body msg)
        payload (json/unsafeParse body)]
    (append! (by-id "updates-holder") (html [:p (.-info payload)]))))

(def amq-url "ws://192.168.5.100:61614/stomp")

(defn on-connect
  [client]
  (.debug client "Connected")
  (.subscribe client "/topic/test" output-message))

(defn stomp-connect! [client]
  (set! (.-debug client) #(append! (by-id "debug") (html [:p %])))
  (.debug client "Connecting...")
  (.connect client "admin" "admin"
            #(on-connect client)
            (fn [err] (.debug client err))))

(defn init-stomp
  "Initializes STOMP infrastructure"
  []
  (let [client (.client js/Stomp amq-url)]
    (stomp-connect! client)
    (listen! (by-id "disconnect")
             :click
             (fn [_]
               (.disconnect client #(.debug client "Disconnected"))))))

(defn ^:export init []
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
    (listen! (by-id "connect")
             :click
             #(init-stomp))))