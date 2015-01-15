### djnativeswing-clj

A [Seesaw](https://github.com/daveray/seesaw) compatible Clojure wrapper of [DJ Native Swing](http://djproject.sourceforge.net/ns/), a collection of native widgets for Swing.

Currently supported widgets:

* web browser (missing support for XPCOM).

If you need a specific widget, but you don't see it in the above list, contact me.

## Introduction

DJ Native Swing is a Java library of Swing widgets implementing OS native GUI components such as a web browser, Adobe Flash player, VLC player and more. DJ Native Swing relies heavily on the [Standard Widget Toolkit (SWT)](https://www.eclipse.org/swt/) to fulfil its duties. Seesaw is a highly usable Clojure wrapper of Swing. The purpose of djnativeswing-clj is to provide a Clojure wrapper of DJ Native Swing as an extension to Seesaw. Indeed this project utilizes the same internal mechanisms as Seesaw so they integrate seamlessly.

## SWT and platform dependence

Since DJ Native Swing depends on SWT you will need to provide a platform-specific implementation of SWT as a dependency of your djnativeswing-clj application. This ultimately implies that you will either have to ship platform-dependent versions of your application or ship all relevant SWT implementations with your application and put the correct one on the classpath before launch, e.g. through a shell script. A Maven repository of SWT releases is available on https://code.google.com/p/swt-repo/ and releases are also available on http://mvnrepository.com/.

## Releases and dependency information

The latest release of djnativeswing-clj is version 0.1.0.

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clojure
[djnativeswing-clj "0.1.0"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>djnativeswing-clj</groupId>
  <artifactId>djnativeswing-clj</artifactId>
  <version>0.1.0</version>
</dependency>
```

Because DJ Native Swing is not distributed through Maven, its JARs are kept in the **custom** repository available at https://github.com/MIvanchev/maven-repository. Please keep in mind that this is not an official repository so there are virtually no security and integrity guarantees whatsoever except my best effort to keep the users of djnativeswing-clj, satisfied.

## Usage

To get started with djnativeswing-clj create a new Leinigen project with `lein new test-proj`. Change to the directory `test-proj` and open the Leiningen configuration file `project.clj`. Add djnativeswing-clj and the SWT implementation for your platform to the dependencies:

```
:dependencies [[org.clojure/clojure "1.6.0"]
               [djnativeswing-clj "0.1.0"]
               [org.eclipse.swt.org.eclipse.swt.win32.win32.x86_64.4.3.swt/org.eclipse.swt.win32.win32.x86_64 "4.3"]]
```

Notice that SWT for Windows is used in the above listing. Now edit the file `src/test-proj/core.clj` to read:

```clojure
(ns test-proj.core
  (:require [djnativeswing-clj.core :as dj]
            [seesaw.core :as sw])
  (:import java.awt.Toolkit))

(defn -main
  [& args]
  (sw/native!)
  (dj/open-native-interface!)
  (.setDynamicLayout (Toolkit/getDefaultToolkit) true)
  (sw/invoke-later
    (sw/frame :width 1024
              :height 768
              :title "DJ Native Swing with seesaw!"
              :content (dj/browser :url "http://www.google.com")
              :on-close :exit
              :visible? true))
  (dj/run-event-pump!))
```

Run the program with `lein run -m test-proj.core` and good fortune will come to you in form of a web browser.

## Demos

DJ Native Swing includes an application with samples of typical usage scenarios. A port of it will soon be available for djnativeswing-clj.

## License

Copyright © 2015 Mihail Ivanchev.

Distributed under the Eclipse Public License, the same as Clojure.

Seesaw is distributed under the Eclipse Public License, the same as Clojure.
DJ Native Swing is distributed under the Lesser GNU Public License (LGPL), version 2.1 or later.
