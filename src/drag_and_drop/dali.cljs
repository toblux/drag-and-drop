(ns drag-and-drop.dali
  (:require [goog.dom :as dom]))

(defn context-2d [canvas]
  "Returns the 2D context from a canvas element."
  (.getContext canvas "2d"))

(defn setup-canvas!
  "Sets up a canvas element by scaling it and its context to the right size."
  [canvas w h]
  (let [ctx (context-2d canvas)
        scale-factor (dom/getPixelRatio)]
    (set! (.-width canvas) (* w scale-factor))
    (set! (.-height canvas) (* h scale-factor))
    (set! (.-width (.-style canvas)) (str w "px"))
    (set! (.-height (.-style canvas)) (str h "px"))
    (set! (.-minWidth (.-style canvas)) (str w "px"))
    (set! (.-minHeight (.-style canvas)) (str h "px"))
    (.scale ctx scale-factor scale-factor)
    ctx))

(defn fill-style! [ctx color]
  "Sets the fill style of a context to the specified color."
  (set! (.-fillStyle ctx) color)
  ctx)

(defn fill-circle!
  "Fills the circle with the current fill style."
  [ctx x y radius]
  (doto ctx
    (.beginPath)
    (.arc x y radius 0 (* 2 (.-PI js/Math)))
    (.closePath)
    (.fill)))
