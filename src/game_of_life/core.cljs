(ns game-of-life.core
  (:use-macros [cljs.core.async.macros :only [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [cljs.core.async :refer [<! timeout]]))

(enable-console-print!)

;; Cells state:
;; N - nothing
;; D - dying
;; A - alive

(defn new-board [n]
  (vec (repeat n (vec (repeat n :N)))))

(def board-size 50)

(defonce app-state
  (atom {:text "Game Of Life"
         :board (new-board board-size)
         :game-running false}))

(defn click-cell! [i j]
  (case (get-in @app-state [:board i j])
    :N (swap! app-state assoc-in [:board i j] :A)
    :A (swap! app-state assoc-in [:board i j] :N)))

(defn count-alive-neighbours [board x y]
  (let [neighbours [[-1 -1] [-1 0] [-1 1] [0 -1]
                    [0 1]   [1 -1] [1 0]  [1 1]]
        get-neighbour (fn [[dx dy]] [(+ x dx) (+ y dy)])]
    (count (filter (fn [x] (or (= x :A) (= x :D)))
                   (map
                    (fn [[x y]] (get-in board [x y]))
                    (map get-neighbour neighbours))))))

(defn mark-dying-cells [board]
  (let [board-atom (atom board)]
   (doseq [i (range board-size)
           j (range board-size)]
    (if (and (= (get-in board [i j]) :A)
             (let [neighbours-alive (count-alive-neighbours board i j)]
               (or (< neighbours-alive 2)    ; lonely
                   (> neighbours-alive 3)))) ; overcrowded
      (swap! board-atom assoc-in [i j] :D)))
  @board-atom))

(defn add-new-cells [board]
  (let [board-atom (atom board)]
    (doseq [i (range board-size)
            j (range board-size)]
      (if (and (= (get-in board [i j]) :N)
               (= (count-alive-neighbours board i j) 3))
        (swap! board-atom assoc-in [i j] :A)))
  @board-atom))

(defn remove-dead-cells [board]
  (let [board-atom (atom board)]
  (doseq [i (range board-size)
          j (range board-size)]
    (if (= (get-in board [i j]) :D)
      (swap! board-atom assoc-in [i j] :N)))
  @board-atom))

(defn run-game! []
  (if (:game-running @app-state)
    (go (<! (timeout 100))
        (do
          (swap! app-state update :board (comp
                                          remove-dead-cells
                                          add-new-cells
                                          mark-dying-cells))
          (run-game!)))))

(defn run-button! []
  (swap! app-state assoc :game-running true)
  (run-game!))

(defn stop-button! []
  (swap! app-state assoc :game-running false))

(defn kill-button! []
  (swap! app-state assoc :game-running false)
  (swap! app-state assoc :board (new-board board-size)))

(defn rectangle [i j]
  [:rect
   {:width 0.9
    :height 0.9
    :fill "lightgrey"
    :x i :y j
    :on-click #(click-cell! i j)}])

(defn circle [i j]
  [:circle
   {:r 0.45
    :fill "red"
    :cx (+ 0.45 i)
    :cy (+ 0.45 j)
    :on-click #(click-cell! i j)}])

(defn draw-cell [i j]
  (let [cell-val (get-in (:board @app-state) [i j])]
    (case cell-val
      :N (rectangle i j)
      :A (circle i j))))

(defn game-of-life! []
  [:center
   [:h1 (:text @app-state) [:board]]
   (into
    [:svg
     {:view-box (string/join " " [0 0 board-size board-size])
      :width 500
      :height 500}]
    (for [i (range board-size)
          j (range board-size)]
      (draw-cell i j)))
   [:p
    [:button
    {:on-click #(run-button!)}
     "Run"]
    [:button
     {:on-click #(stop-button!)}
     "Stop"]
    [:button
     {:on-click #(kill-button!)}
      "Kill"]]])

(reagent/render-component [game-of-life!]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
