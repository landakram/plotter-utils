# plotter-utils

[![Clojars Project](https://img.shields.io/clojars/v/plotter-utils.svg)](https://clojars.org/plotter-utils)

Utilties for working with HPGL plotters and quil.

## Usage

`plotter-utils.quil` contains utilities for outputing quil sketches as HPGL
instructions that are centered and scaled.

`do-record` can be used to record quil sketches as HPGL instructions. Sketches
are scaled preserving aspect ratio to a plotter size of 11040x7721. This can be
further scaled to the actual plotter size using IP and SC HPGL instructions.

```clj
(defn draw-state [{:keys [x1 y1 x2 y2] :as state}]
  (let [out "generated/out.hpgl"]
    (plotter-utils.quil/do-record
     (width)
     (height)
     out
     (fn []
       (println "Recording to hpgl...")
       (line x1 y1 x2 y2)
       (println "Done.")))
    (no-loop)))
```

`plotter-utils.grid` contains utilities for creating grids:

```clj
(defn initial-state []
  (let [width (width)
        height (height)
        tile-width 20
        tile-height 20
        origin [0 0]
        grid (make-grid tile-width tile-height width height origin)]
    {:grid grid
     :x-start (random 10)
     :y-start (random 10)}))

(defn draw-state [{:keys [x-start y-start grid]}]
  (let [{:keys [tile-width tile-height]} grid
        grid-points (grid-points grid)]
    (doseq [[x y] grid-points]
      (let [x-noise (mul-add x 0.0030 x-start)
            y-noise (mul-add y 0.0030 y-start)
            noise-factor (noise x-noise y-noise (* (frame-count) 0.04))]
        (draw-line x y
                   tile-width
                   tile-height
                   noise-factor)))))
```

`randomly-subdivided-tiles` is a convenience function for iterating through
the tiles of a randomly subdivided grid:

```clj
(def g (make-grid [0 0] 1000 1000 10 10))
(take 3 (randomly-subdivided-tiles g))
=> ({:origin [0 0],
     :width 500,
     :height 500,
     :tile-width 10,
     :tile-height 10}
    {:origin [0 500],
     :width 250,
     :height 250,
     :tile-width 10,
     :tile-height 10}
    {:origin [250 500],
     :width 250,
     :height 250,
     :tile-width 10,
     :tile-height 10})
```

## License

Copyright © 2020 Mark Hudnall

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
