(defproject plotter-utils "0.1.2"
  :description "Utilties for working with HPGL plotters and quil."
  :url "https://github.com/landakram/plotter-utils"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [quil "3.1.0"]]
  :source-paths ["src"]
  :java-source-paths ["src/java"]
  :repl-options {:init-ns plotter-utils.quil})
