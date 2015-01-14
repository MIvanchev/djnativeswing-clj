;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns djnativeswing-clj.browser-test
  (:use clojure.test
        djnativeswing-clj.browser
        [djnativeswing-clj.core :only [browser]]
        [seesaw.core :only [invoke-now]])
  (:import [chrriis.dj.nativeswing.swtimpl.components
            JWebBrowser
            JWebBrowser$WebBrowserDecoratorFactory
            WebBrowserDecorator]
           java.awt.Component))

(def to-decorator-factory #'djnativeswing-clj.browser/to-decorator-factory)

(defn decorator
  []
  (proxy [WebBrowserDecorator] []
    (configureForWebBrowserWindow [webBrowserWindow]
      nil)
    (isButtonBarVisible []
      false)
    (isLocationBarVisible []
      false)
    (isMenuBarVisible []
      false)
    (isStatusBarVisible []
      false)
    (setButtonBarVisible [isButtonBarVisible]
      nil)
    (setLocationBarVisible [isLocationBarVisible]
      nil)
    (setMenuBarVisible [isMenuBarVisible]
      nil)
    (setStatusBarVisible [isStatusBarVisible]
      nil)))

(deftest decorator-factory-from-fn
  (let [decorator (decorator)
        factory   (to-decorator-factory
                    (fn [browser rendering-component] decorator))
        browser   (browser)
        component (proxy [Component] [])]
    (is (instance? JWebBrowser$WebBrowserDecoratorFactory factory))
    (is (= decorator (.createWebBrowserDecorator factory browser component)))))

(deftest decorator-factory-from-class
  (let [decorator     (decorator)
        factory-class (proxy [JWebBrowser$WebBrowserDecoratorFactory] []
                        (createWebBrowserDecorator [browser rendering-component]
                          decorator))]
    (is (= factory-class (to-decorator-factory factory-class)))))

(deftest decorator-factory-from-misc
  (let [msg #"Don't know how to make WebBrowserDecoratorFactory from:"]
    (is (thrown? IllegalArgumentException (to-decorator-factory nil) msg))
    (is (thrown? IllegalArgumentException (to-decorator-factory "foo") msg))
    (is (thrown? IllegalArgumentException (to-decorator-factory true) msg))))

(deftest info-test
  (let [info (invoke-now (info (browser)))]
    (is (= (count (keys info)) 2))
    (is (contains? info :type))
    (is (contains? info :version))))
