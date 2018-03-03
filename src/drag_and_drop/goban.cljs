(ns drag-and-drop.goban
  (:require [drag-and-drop.dali :as dali]
            [cljs.spec.alpha :as s]))

(def black-stone 'b)
(def white-stone 'w)

(defn black-stone? [stone] (= stone black-stone))
(defn white-stone? [stone] (= stone white-stone))

(defn stone-image [radius white?]
  (let [diameter (* 2 radius)
        canvas (.createElement js/document "canvas")
        ctx (dali/setup-canvas! canvas diameter diameter)]
    (-> ctx
      (dali/fill-style! "#333")
      (dali/fill-circle! radius radius radius)
      (dali/fill-style! (if white? "white" "black"))
      (dali/fill-circle! radius radius (dec radius)))
    (.toDataURL canvas)))

;;; Spec

(s/def ::stone #(or (black-stone? %) (white-stone? %)))
(s/def ::board (s/coll-of (s/nilable ::stone)))
(s/def ::board-size #(contains? #{7 9 13 19} %))

;;;

(defn- valid-position?
  "Checks if a position is within a boards' bounds."
  [board position]
  {:pre  [(s/valid? ::board board)]
   :post [(boolean? %)]}
  (and (integer? position) (<= 0 position (count board))))

(defn empty-board
  "Returns an empty board."
  [size]
  {:pre  [(s/valid? ::board-size size)]
   :post [(s/valid? ::board %)]}
  (vec (replicate (* size size) nil)))

(defn contains-empty-fields?
  "Checks if the board contains an empty field."
  [board]
  {:pre  [(s/valid? ::board board)]
   :post [(boolean? %)]}
  (some nil? board))

(defn- empty-fields
  "Returns a vector of empty field indices."
  [board]
  {:pre  [(s/valid? ::board board)]
   :post [(vector? %) (every? integer? %)]}
  (vec
    (map first
      (filter #(nil? (second %))
        (map-indexed vector board)))))

(defn- field-empty?
  "Checks if a given position is empty."
  [board position]
  {:pre  [(s/valid? ::board board)
          (valid-position? board position)]
   :post [(boolean? %)]}
  (not (get board position)))

(defn add-stone
  "Places a stone on the board."
  [board position color]
  {:pre  [(s/valid? ::board board)
          (valid-position? board position)]
   :post [(s/valid? ::board %)]}
  (assoc board position color))

(defn rem-stone
  "Removes a stone from the board."
  [board position]
  {:pre  [(s/valid? ::board board)
          (valid-position? board position)]
   :post [(s/valid? ::board %)]}
  (assoc board position nil))

(defn random-place-stone
  "Places a stone randomly on an open field. Returns the original board if there is no open field."
  [board color]
  {:pre  [(s/valid? ::board board)
          (s/valid? ::stone color)]
   :post [(s/valid? ::board %)]}
  (if (contains-empty-fields? board)
    (let [empty-field-indices (empty-fields board)
          random-index (rand-int (count empty-field-indices))
          random-position (get empty-field-indices random-index)]
      (add-stone board random-position color))
    board))

(defn random-place-stones
  "Randomly places stones on open fields."
  [board count white?]
  {:pre  [(s/valid? ::board board)
          (integer? count)
          (boolean? white?)]
   :post [(s/valid? ::board %)]}
  (if (> count 0)
    (let [color (if white? white-stone black-stone)]
      (random-place-stones
        (random-place-stone board color) (dec count) (not white?)))
    board))

(defn random-board
  "Returns a random board."
  [size stone-count]
  {:pre  [(s/valid? ::board-size size)
          (<= 0 stone-count (* size size))]
   :post [(s/valid? ::board %)]}
  (random-place-stones (empty-board size) stone-count true))
