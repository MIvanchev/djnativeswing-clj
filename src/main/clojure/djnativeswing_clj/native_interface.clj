;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns ^{:doc "Holds functions for controlling the native interface, a mechanism
           which the DJ Native Swing widgets mostly depend on.

           DJ Native Swing widgets are implemented using native components
           (through SWT). This requires communicational infrastructure which the
           native interface provides. You will typically want only the aliases
           provided in djnativeswing-clj.core.

           See:
             http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis.dj.nativeswing.swtimpl.NativeInterface
           "
      :author "Mihail Ivanchev"}
  djnativeswing-clj.native-interface
  (:import chrriis.dj.nativeswing.swtimpl.NativeInterface))

(defn initialize!
  "Initializes the native interface if not already done. This function doesn't
  open the interface after the initialization. The native interface should be
  initialized and opened before any widgets are created and used.

  See:
    (djnativeswing-clj.native-interface/open!)
  "
  []
  (NativeInterface/initialize))

(defn open!
  "Opens the native interface and initializes it if not already done. The native
  interface should be initialized and opened before any widgets are created and
  used. Prefer the alias djnativeswing-clj.core/open-native-interface!.

  See:
    (djnativeswing-clj.native-interface/initialize!)
    (djnativeswing-clj.native-interface/close!)
  "
  []
  (NativeInterface/open))

(defn close!
  "Closes the native interface. Prefer the alias
  djnativeswing-clj.core/close-native-interface!."
  []
  (NativeInterface/close))

(defn run-event-pump!
  "Runs the native event pump which is required for the correct operation of the
  DJ Native Swing widgets on some platforms. It should always be called after
  the native interface is initialized."
  []
  (NativeInterface/runEventPump))

(defn initialized?
  "Returns true iff the native interface has been initialized."
  []
  (NativeInterface/isInitialized))

(defn open?
  "Returns true iff the native interface has been opened."
  []
  (NativeInterface/isOpen))

(defn event-pump-running?
  "Returns true iff the event pump is currently running."
  []
  (NativeInterface/isEventPumpRunning))

(defn ui-thread?
  "Returns true if the current thread is the UI thread of the native side when
  the parameter is true or of the Swing side when the parameter is false."
  [native-side]
  (NativeInterface/isUIThread native-side))
