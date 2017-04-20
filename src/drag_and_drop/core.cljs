(ns drag-and-drop.core
  (:require [drag-and-drop.goban :as goban]
            [clojure.string :as str]
            [om.core :as om]
            [om.dom :as dom]))

(enable-console-print!)

;;; App State

(def number-of-rows 7)
(def number-of-stones 10)

(defonce app-state
  (atom {:board (goban/random-board number-of-rows number-of-stones)}))

;;; Helpers

(defn square [x]
  (.pow js/Math x 2))

(def image-dir "/images/")

(defn image-src [filename]
  (str image-dir filename ".png"))

(defn images-srcset [filename]
  (str/join ","
    [(str image-dir filename "@2x.png 2x")
     (str image-dir filename "@3x.png 3x")]))

;;; Drag and Drop

(defn handle-drag-start [event owner position color]
  (let [dt (.-dataTransfer event)]
    (set! (.-effectAllowed dt) "move")
    (doto dt
      (.setData "start-position" position)
      (.setData "color" color)))
  (om/set-state! owner :dragging? true))

(defn handle-drag-end [event owner]
  (om/set-state! owner :dragging? false))

(defn handle-drop [event data position]
  (.preventDefault event)
  (let [dt (.-dataTransfer event)
        start-position (js/parseInt (.getData dt "start-position"))
        color (symbol (.getData dt "color"))
        board (:board data)]
    (when (goban/field-empty? board position)
      (om/update! data :board
        (-> board
          (goban/rem-stone start-position)
          (goban/add-stone position color))))))

;;; Goban

(defn background-grid-cells [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "background-grid-cells"}
        (for [i (range (square (dec number-of-rows)))]
          (dom/div #js {:className "background-cell" :key i}))))))

(defn background-grid-decoration [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "background-grid-border"}
        (dom/div #js {:className "background-grid-dot"})))))

(defn draggable-stone [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:dragging? false})

    om/IRenderState
    (render-state [_ {:keys [dragging? position color]}]
      (dom/img #js
        {:className "stone"
         :style #js {:opacity (when dragging? 0)}
         :onDragStart #(handle-drag-start % owner position color)
         :onDragEnd #(handle-drag-end % owner)
         :srcSet (images-srcset color)
         :src (image-src color)
         :draggable true}))))

(defn interactive-grid [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:board (:board data)})

    om/IWillReceiveProps
    (will-receive-props [_ next-props]
      (om/set-state! owner :board (:board next-props)))

    om/IRenderState
    (render-state [_ {:keys [board]}]
      (dom/div #js {:className "interactive-grid"}
        (for [position (range (square number-of-rows))]
          (dom/div #js
            {:className "interactive-cell"
             :onDragOver #(.preventDefault %)
             :onDrop #(handle-drop % data position)
             :key position}
            (when-let [color (get board position)]
              (om/build draggable-stone data
                {:state {:position position :color color}}))))))))

(defn board [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "board"}

        ;; Background grid and decoration
        (om/build background-grid-cells data)
        (om/build background-grid-decoration data)

        ;; Interactive grid and stones
        (om/build interactive-grid data)))))

(om/root board app-state
  {:target (.querySelector js/document "#drag-and-drop")})
