(ns plotter-utils.quil
  (:import [hpglgraphics HPGLGraphics])
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [quil.applet :as ap]
            [quil.core :as q]
            [quil.util :as u]))

(def doc-width 11040)
(def doc-height 7721)

(defn create-hpgl-graphics [w h path]
  (.createGraphics (ap/current-applet) (int w) (int h)
                   HPGLGraphics/HPGL
                   (u/absolute-path path)))

(defn strip-init-instruction [hpgl-cmds]
  (string/replace hpgl-cmds "IN;" ""))

(defn do-record
  "Record quil sketch as HPGL instructions in outfile. w and h are scaled preserving aspect
  ratio to fit a 11040x7721 plotter, since that's what the underlying implementation,
  HPGLGraphics, expects. This can be further scaled by the plotter / plot.py using IP and
  SC instructions."
  [w h outfile f]
  (let [scale-val (if (> (/ w h) (/ doc-width doc-height))
                    (/ doc-width w)
                    (/ doc-height h))
        new-w (* w scale-val)
        new-h (* h scale-val)
        padding-w (/ (- doc-width new-w) 2)
        padding-h (/ (- doc-height new-h) 2)] 
    (q/do-record (create-hpgl-graphics doc-width doc-height outfile)
                 (q/translate padding-w padding-h)
                 (q/scale scale-val scale-val)
                 (f))
    (spit outfile (strip-init-instruction (slurp outfile)))))
