(ns sin-wave.shopping
  (:require [domina.core :refer [append!
                                 by-class
                                 by-id
                                 destroy!
                                 set-value!
                                 value]]
            [domina.events :refer [listen!]]
            [hiccups.runtime])
  (:require-macros [hiccups.core :refer [html]]))

(defn calculate []
  (let [quantity (value (by-id "quantity"))
        price (value (by-id "price"))
        tax (value (by-id "tax"))
        discount (value (by-id "discount"))]
    (set-value! (by-id "total") (-> (* quantity price)
                                    (* (+ 1 (/ tax 100)))
                                    (- discount)
                                    (.toFixed 2)))))

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
               (destroy! (by-class "help"))))))

(set! (.-onload js/window) init)