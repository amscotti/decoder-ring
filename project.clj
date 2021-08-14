(defproject decoder-ring "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :main decoder-ring.core
  :aot :all
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/test.check "1.1.0"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :repl-options {:init-ns decoder-ring.core})
