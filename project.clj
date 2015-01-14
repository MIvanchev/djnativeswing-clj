(defproject djnativeswing-clj "0.1.0-SNAPSHOT"
  :description "A Seesaw compatible Clojure wrapper of DJ Native Swing. See https://github.com/MIvanchev/djnativeswing-clj, http://djproject.sourceforge.net/ns/, http://seesaw-clj.org for more info."
  :url "https://github.com/MIvanchev/djnativeswing-clj"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :global-vars {*warn-on-reflection* true}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [djnativeswing/djnativeswing "1.0.2"]
                 [djnativeswing/djnativeswing-swt "1.0.2"]
                 [seesaw "1.4.5"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]
  :profiles {:dev {:dependencies [[org.eclipse.swt.org.eclipse.swt.win32.win32.x86_64.4.3.swt/org.eclipse.swt.win32.win32.x86_64 "4.3"]]}}
  :codox {})
