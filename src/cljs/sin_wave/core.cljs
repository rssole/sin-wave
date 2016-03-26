;; create the main project namespace
(ns sin-wave.core)

;; enable cljs to print to the JS console of the browser
(enable-console-print!)

(def canvas (.getElementById js/document "myCanvas"))
(def ctx    (.getContext canvas "2d"))

;; Clear canvas before doing anything else
(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))

(def interval   js/Rx.Observable.interval)
(def sw-time       (interval 10))

(defn deg-to-rad [n]
  (* (/ Math/PI 180) n))

(defn sine-coord [x]
  (let [sin (Math/sin (deg-to-rad x))
        y   (- 100 (* sin 90))]
    {:x   x
     :y   y
     :sin sin}))

; export signals cljs compiler not to mungle name thus it
; will be accessible from JS directly
(def ^:export sine-wave
  (.map sw-time sine-coord))

(defn fill-rect [x y colour]
  (set! (.-fillStyle ctx) colour)
  (.fillRect ctx x y 2 2))

#_(-> sine-wave
      (.take 600)
      (.subscribe (fn [{:keys [x y]}]
                    (fill-rect x y "cyan"))))

(def colour (.map sine-wave
                  (fn [{:keys [sin]}]
                    (if (< sin 0)
                      "red"
                      "blue"))))

#_(-> (.zip sine-wave colour #(vector % %2))
      (.take 600)
      (.subscribe (fn [[{:keys [x y]} colour]]
                    (fill-rect x y colour))))

(def red  (.map sw-time (fn [_] "red")))
(def blue (.map sw-time (fn [_] "blue")))
(def green (.map sw-time (fn [_] "green")))
(def yellow (.map sw-time (fn [_] "yellow")))
(def purple (.map sw-time (fn [_] "purple")))

(def sw-concat     js/Rx.Observable.concat)
(def defer      js/Rx.Observable.defer)
(def from-event js/Rx.Observable.fromEvent)

(def mouse-click (from-event canvas "click"))

(def cycle-colour
  (sw-concat (.takeUntil red mouse-click)
          (defer #(sw-concat (.takeUntil blue mouse-click)
                          cycle-colour))))

(def flat-map js/Rx.Observable.flatMap)
(def sw-repeat js/Rx.Observable.repeat)
(def scan js/Rx.Observable.scan)

(-> (.zip sine-wave cycle-colour #(vector % %2))
    (.take 600)
    (.subscribe (fn [[{:keys [x y]} colour]]
                  (fill-rect x y colour))))