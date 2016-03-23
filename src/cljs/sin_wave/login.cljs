(ns sin-wave.login
  (:require [domina.core :refer [by-id value set-value!]]
            [domina.events :refer [listen! prevent-default]]))

;; define the function to be attached to form submission event(
(defn validate-form [e]
  (if (or (empty? (value (by-id "email")))
          (empty? (value (by-id "password"))))
    (do
      (prevent-default e)
      (js/alert "Please, complete the form!"))
    true))

;; define the function to attach validate-form to onsubmit event of
;; the form
(defn init []
  (if (and js/document
           (aget js/document "getElementById"))
    (listen! (by-id "submit") :click (fn [e] (validate-form e)))))

;; initialize the HTML page in unobtrusive way
(set! (.-onload js/window) init)