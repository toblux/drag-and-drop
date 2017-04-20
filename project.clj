(defproject drag-and-drop "0.1.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]
                 [org.omcljs/om "1.0.0-alpha48"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.9"]]
  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js"
                                    ".lein-repl-history"
                                    "figwheel_server.log"]
  :cljsbuild {:builds
              [{:id "debug"
                :source-paths ["src"]
                :figwheel true
                :compiler {:output-to "resources/public/js/drag-and-drop.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :source-map true}}
               {:id "release"
                :source-paths ["src"]
                :compiler {:elide-asserts true
                           :pretty-print false
                           :output-to "resources/public/js/drag-and-drop.min.js"
                           :optimizations :advanced}}]}
  :figwheel {:css-dirs ["resources/public/css"]})
