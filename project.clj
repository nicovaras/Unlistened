(defproject player "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                  [org.clojars.technomancy/jlayer "1.0"]
                  [org.clojars.automata/tritonus-share "1.0.0"]
                  [org.clojars.automata/mp3spi "1.9.4"]
                  [clj-glob "1.0.0"]
                  [org.clojars.wmealing/clj-audio "0.2.0-SNAPSHOT"]]
  :main ^:skip-aot player.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
