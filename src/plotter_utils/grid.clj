(ns plotter-utils.grid
  (:require [quil.helpers.seqs :refer [range-incl]]))

(defn make-grid [origin width height tile-width tile-height]
  {:origin origin
   :width width
   :height height
   :tile-width tile-width
   :tile-height tile-height})

(defn grid-points [{:keys [origin width height tile-width tile-height]}]
  (let [[origin-x origin-y] origin]
    (for [y (range-incl origin-x (+ origin-x height) tile-height)
          x (range-incl origin-y (+ origin-y width) tile-width)]
        [x y])))

;; Note: this leaves tile-width and tile-height in the "world's" scale.
;; It might be useful to scale the tile-width/height down as well, exposing
;; both as properties.
;;
;; Also, sections is a misnomer...
(defn create-tiles [grid sections]
  (let [tile-width (/ (:width grid) sections)
        tile-height (/ (:height grid) sections)]
    (map
     (fn [point]
       (merge grid {:origin point :width tile-width :height tile-height}))
     (grid-points (merge grid {:tile-width tile-width :tile-height tile-height})))))

(defn randomly-subdivide [tiles & {:keys [prob] :or {prob 0.5}}]
  (flatten
   (map
    (fn [tile]
      (if (< (rand 1) prob)
        (create-tiles tile 2)
        [tile]))
    tiles)))

(defn randomly-subdivided-tiles [grid & {:keys [rounds prob] :or {rounds 3 prob 0.5}}]
  (let [tiles (create-tiles grid 2)]
    (nth (iterate #(randomly-subdivide % :prob prob) tiles) rounds)))
