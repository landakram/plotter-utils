(ns plotter-utils.quil
  (:import [hpglgraphics HPGLGraphics])
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [quil.applet :as ap]
            [quil.core :as q]
            [quil.util :as u]))

(println "~ ~ * * plotter-utils * * ~ ~")

(def doc-width 11040)
(def doc-height 7721)

(defn create-hpgl-graphics [w h path]
  (-> (.createGraphics (ap/current-applet) (int w) (int h)
                       HPGLGraphics/HPGL
                       (u/absolute-path path))
      (.useFloor)))

(defn strip-init-instruction [hpgl-cmds]
  (string/replace hpgl-cmds "IN;" ""))

(defn do-record
  "Record quil sketch as HPGL instructions in outfile. w and h are not scaled, so the units in the
  outputted HPGL file are those which are used by quil. These can be further scaled by using IP and
  SC instructions."
  [w h outfile f]
  (let [doc-width w
        doc-height h
        scale-val (if (> (/ w h) (/ doc-width doc-height))
                    (/ doc-width w)
                    (/ doc-height h))
        new-w (* w scale-val)
        new-h (* h scale-val)
        padding-w (/ (- doc-width new-w) 2)
        padding-h (/ (- doc-height new-h) 2)] 
    (q/do-record (create-hpgl-graphics doc-width doc-height outfile)
                 (q/clip padding-w padding-h new-w new-h)
                 (q/translate padding-w padding-h)
                 (q/scale scale-val scale-val)
                 (f))
    (spit outfile (strip-init-instruction (slurp outfile)))))

(defn record-many
  "Given a seq of quil `states`, call `f` repeatedly with each state,
  recording each call into HPGL instructions. HPGL instructions are
  outputted as separate files using the optional parameter `out` as
  a template. `f` should accept two arguments: `state`, the current
  iteration of `states`, and `out`, the path to the current to-be-written
  outfile."
  [states w h f & {:keys [out] :or {out "generated/out-####.hpgl"}}]
  (seq
   (map-indexed
    (fn [idx state]
      (let [out (clojure.string/replace out "####" (str idx))]
        (do-record w h out #(f state out))))
    states)))
