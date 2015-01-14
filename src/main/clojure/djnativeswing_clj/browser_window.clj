;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns ^{:doc "Holds functions for interacting with browser popup windows.

           See:
             http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis/dj/nativeswing/swtimpl/components/JWebBrowserWindow.html
           "
      :author "Mihail Ivanchev"}
  djnativeswing-clj.browser-window
  (:import [chrriis.dj.nativeswing.swtimpl.components
            JWebBrowserWindow]))

(defn dispose!
  "Disposes the web browser popup window by freeing all resources associated
  with it. A typical usage scenario is to prevent a popup window from opening
  and displaying the browser widget in another manner, e.g. in a browser tab."
  [^JWebBrowserWindow target]
  (.dispose target)
  target)
