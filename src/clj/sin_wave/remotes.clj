(ns sin-wave.remotes
  (:require [sin-wave.core :refer [handler]]
            [compojure.handler :refer [site]]
            [shoreleave.middleware.rpc :refer [defremote wrap-rpc]]))

(defremote calculate [quantity price tax discount]
           (let [qn (Float/parseFloat quantity)
                 pr (Float/parseFloat price)
                 tx (Float/parseFloat tax)
                 di (Float/parseFloat discount)]
             (-> (* qn pr)
                (* (+ 1 (/ tx 100)))
                (- di))))

(def app (-> (var handler)
             (wrap-rpc)
             (site)))
