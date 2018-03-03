(defproject drag-and-drop "0.1.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.64"]
                 [org.omcljs/om "1.0.0-beta1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.15"]]
  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js"
                                    ".lein-repl-history"
                                    "figwheel_server.log"]
  :cljsbuild {:builds
              [{:id "debug"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main "drag-and-drop.core"
                           :asset-path "js"
                           :output-to "resources/public/js/drag-and-drop.js"
                           :output-dir "resources/public/js"
                           :optimizations :none
                           :source-map true}}
               {:id "release"
                :source-paths ["src"]
                :compiler {:elide-asserts true
                           :pretty-print false
                           :output-to "resources/public/js/drag-and-drop.js"
                           :optimizations :advanced}}]}
  :figwheel {:css-dirs ["resources/public/css"]})
