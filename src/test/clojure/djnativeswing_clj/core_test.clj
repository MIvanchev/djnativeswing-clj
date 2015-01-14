;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns djnativeswing-clj.core-test
  (:use clojure.test
        djnativeswing-clj.core
        [seesaw.core :only [config]]
        [seesaw.options :only [apply-options]])
  (:import  [chrriis.dj.nativeswing.swtimpl.components
             Credentials
             JWebBrowser
             JWebBrowserWindow
             WebBrowserAuthenticationHandler]
            java.awt.Dimension))

(def to-authentication-handler #'djnativeswing-clj.core/to-authentication-handler)

(deftest authentication-handler-from-fn
  (let [handler  (to-authentication-handler
                   (fn [browser resource-location]
                     ["John" "123456"]))
        browser  (browser)
        location "http://www.google.com"]
    (is (instance? WebBrowserAuthenticationHandler handler))
    (is (= (.getUserName (.getCredentials handler browser location)) "John"))
    (is (= (.getPassword (.getCredentials handler browser location)) "123456"))))

(deftest authentication-handler-from-class
  (let [handler (proxy [WebBrowserAuthenticationHandler] []
                  (getCredentials [browser resource-location]
                    (Credentials. "John" "123456")))]
    (is (= handler (to-authentication-handler handler)))))

(deftest authentication-handler-from-misc
  (let [msg #"Don't know how to make WebBrowserAuthenticationHandler from:"]
    (is (thrown? IllegalArgumentException (to-authentication-handler nil) msg))
    (is (thrown? IllegalArgumentException (to-authentication-handler "a") msg))
    (is (thrown? IllegalArgumentException (to-authentication-handler 1) msg))))

(defn browser-inst
  [& opts]
  (apply-options (JWebBrowser. nil ) opts))

(deftest browser-opt-bars-visible?
  (testing "Setting the :bars-visible? option"
    (testing "does nothing when omitted."
      (let [browser (browser-inst)]
        (is (.isButtonBarVisible browser))
        (is (.isLocationBarVisible browser))
        (is (.isMenuBarVisible browser))
        (is (.isStatusBarVisible browser))))
    (testing "shows all bars when provided a truthy value."
      (let [browser (browser-inst :bars-visible? true)]
        (is (.isButtonBarVisible browser))
        (is (.isLocationBarVisible browser))
        (is (.isMenuBarVisible browser))
        (is (.isStatusBarVisible browser))))
    (testing "hides all bars when provided a falsey value."
      (let [browser (browser-inst :bars-visible? false)]
        (is (not (.isButtonBarVisible browser)))
        (is (not (.isLocationBarVisible browser)))
        (is (not (.isMenuBarVisible browser)))
        (is (not (.isStatusBarVisible browser)))))))

(deftest browser-opt-button-bar-visible?
  (testing "Setting the :button-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isButtonBarVisible (browser-inst))))
    (testing "shows the button bar when provided a truthy value."
      (is (.isButtonBarVisible (browser-inst :button-bar-visible? true))))
    (testing "hides the button bar when provided a falsey value."
      (let [browser (browser-inst :button-bar-visible? false)]
        (is (not (.isButtonBarVisible browser))))))
  (testing "Getting the :button-bar-visible? option"
    (testing "returns the value of .isButtonBarVisible"
      (let [browser (doto (browser-inst) (.setButtonBarVisible false))]
        (is (false? (config browser :button-bar-visible?))))
      (let [browser (doto (browser-inst) (.setButtonBarVisible true))]
        (is (true? (config browser :button-bar-visible?)))))))

(deftest browser-opt-location-bar-visible?
  (testing "Setting the :location-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isLocationBarVisible (browser-inst))))
    (testing "shows the location bar when provided a truthy value."
      (is (.isLocationBarVisible (browser-inst :location-bar-visible? true))))
    (testing "hides the location bar when provided a falsey value."
      (let [browser (browser-inst :location-bar-visible? false)]
        (is (not (.isLocationBarVisible browser))))))
  (testing "Getting the :location-bar-visible? option"
    (testing "returns the value of .isLocationBarVisible"
      (let [browser (doto (browser-inst) (.setLocationBarVisible false))]
        (is (false? (config browser :location-bar-visible?))))
      (let [browser (doto (browser-inst) (.setLocationBarVisible true))]
        (is (true? (config browser :location-bar-visible?)))))))

(deftest browser-opt-menu-bar-visible?
  (testing "Setting the :menu-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isMenuBarVisible (browser-inst))))
    (testing "shows the menu bar when provided a truthy value."
      (is (.isMenuBarVisible (browser-inst :menu-bar-visible? true))))
    (testing "hides the menu bar when provided a falsey value."
      (let [browser (browser-inst :menu-bar-visible? false)]
        (is (not (.isMenuBarVisible browser))))))
  (testing "Getting the :menu-bar-visible? option"
    (testing "returns the value of .isMenuBarVisible"
      (let [browser (doto (browser-inst) (.setMenuBarVisible false))]
        (is (false? (config browser :menu-bar-visible?))))
      (let [browser (doto (browser-inst) (.setMenuBarVisible true))]
        (is (true? (config browser :menu-bar-visible?)))))))

(deftest browser-opt-status-bar-visible?
  (testing "Setting the :status-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isStatusBarVisible (browser-inst))))
    (testing "shows the status bar when provided a truthy value."
      (is (.isStatusBarVisible (browser-inst :status-bar-visible? true))))
    (testing "hides the status bar when provided a falsey value."
      (let [browser (browser-inst :status-bar-visible? false)]
        (is (not (.isStatusBarVisible browser))))))
  (testing "Getting the :status-bar-visible? option"
    (testing "returns the value of .isStatusBarVisible"
      (let [browser (doto (browser-inst) (.setStatusBarVisible false))]
        (is (false? (config browser :status-bar-visible?))))
      (let [browser (doto (browser-inst) (.setStatusBarVisible true))]
        (is (true? (config browser :status-bar-visible?)))))))

(deftest browser-opt-authentication-handler
  (testing "Setting the :authentication-handler option"
    (testing "does nothing when omitted."
      (is (nil? (.getAuthenticationHandler (browser-inst)))))
    (testing "sets the authentication handler when provided a suitable value."
      (let[handler     (to-authentication-handler (fn [_ _] ["root" "1234"]))
           browser     (browser-inst :authentication-handler handler)
           credentials (.getCredentials
                         (.getAuthenticationHandler browser)
                         browser
                         "http://www.google.com")]
        (is (= "root" (.getUserName credentials)))
        (is (= "1234" (.getPassword credentials)))))))

(defn browser-win-inst
  [& opts]
  (let [browser (browser)
        state   (atom {:button-bar-visible? true
                       :location-bar-visible? true
                       :menu-bar-visible? true
                       :status-bar-visible? true
                       :title ""
                       :size (Dimension. 320 240)
                       :visible? true})]
    (apply-options
      (proxy [JWebBrowserWindow] []
        (dispose []
          nil)
        (getWebBrowser []
          browser)
        (setButtonBarVisible [isButtonBarVisible]
          (swap! state assoc :button-bar-visible? isButtonBarVisible))
        (isButtonBarVisisble []
          (:button-bar-visible? @state))
        (setLocationBarVisible [isLocationBarVisible]
          (swap! state assoc :location-bar-visible? isLocationBarVisible))
        (isLocationBarVisisble []
          (:location-bar-visible? @state))
        (setMenuBarVisible [isMenuBarVisible]
          (swap! state assoc :menu-bar-visible? isMenuBarVisible))
        (isMenuBarVisisble []
          (:menu-bar-visible? @state))
        (setStatusBarVisible [isStatusBarVisible]
          (swap! state assoc :status-bar-visible? isStatusBarVisible))
        (isStatusBarVisisble []
          (:status-bar-visible? @state))
        (setBarsVisible [areBarsVisible]
          (doto this
            (.setButtonBarVisible areBarsVisible)
            (.setLocationBarVisible areBarsVisible)
            (.setMenuBarVisible areBarsVisible)
            (.setStatusBarVisible areBarsVisible)))
        (setIconImage [image]
          (swap! state assoc :icon-image image))
        (setLocation [location]
          (swap! state assoc :location location))
        (setSize [size]
          (swap! state assoc :size size))
        (getSize []
          (:size @state))
        (setTitle [title]
          (swap! state assoc :title title))
        (getTitle []
          (:title @state))
        (setVisible [isVisible]
          (swap! state assoc :visible? isVisible)))
      opts)))

(deftest browser-win-opt-browser
  (testing "Getting the :browser option returns the browser component."
    (is (instance? JWebBrowser (config (browser-win-inst) :browser)))))

(deftest browser-win-opt-bars-visible?
  (testing "Setting the :bars-visible? option"
    (testing "does nothing when omitted."
      (let [win (browser-win-inst)]
        (is (.isButtonBarVisisble win))
        (is (.isLocationBarVisisble win))
        (is (.isMenuBarVisisble win))
        (is (.isStatusBarVisisble win))))
    (testing "shows all bars when provided a truthy value."
      (let [win (browser-win-inst :bars-visible? true)]
        (is (.isButtonBarVisisble win))
        (is (.isLocationBarVisisble win))
        (is (.isMenuBarVisisble win))
        (is (.isStatusBarVisisble win))))
    (testing "hides all bars when provided a falsey value."
      (let [win (browser-win-inst :bars-visible? false)]
        (is (not (.isButtonBarVisisble win)))
        (is (not (.isLocationBarVisisble win)))
        (is (not (.isMenuBarVisisble win)))
        (is (not (.isStatusBarVisisble win)))))))

(deftest browser-win-opt-button-bar-visible?
  (testing "Setting the :button-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isButtonBarVisisble (browser-win-inst))))
    (testing "shows the button bar when provided a truthy value."
      (is (.isButtonBarVisisble (browser-win-inst :button-bar-visible? true))))
    (testing "hides the button bar when provided a falsey value."
      (let [win (browser-win-inst :button-bar-visible? false)]
        (is (not (.isButtonBarVisisble win))))))
  (testing "Getting the :button-bar-visible? option"
    (testing "returns the value of .isButtonBarVisisble"
      (let [win (doto (browser-win-inst) (.setButtonBarVisible false))]
        (is (false? (config win :button-bar-visible?))))
      (let [win (doto (browser-win-inst) (.setButtonBarVisible true))]
        (is (true? (config win :button-bar-visible?)))))))

(deftest browser-win-opt-location-bar-visible?
  (testing "Setting the :location-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isLocationBarVisisble (browser-win-inst))))
    (testing "shows the location bar when provided a truthy value."
      (is (.isLocationBarVisisble (browser-win-inst :location-bar-visible? true))))
    (testing "hides the location bar when provided a falsey value."
      (let [win (browser-win-inst :location-bar-visible? false)]
        (is (not (.isLocationBarVisisble win))))))
  (testing "Getting the :location-bar-visible? option"
    (testing "returns the value of .isLocationBarVisisble"
      (let [win (doto (browser-win-inst) (.setLocationBarVisible false))]
        (is (false? (config win :location-bar-visible?))))
      (let [win (doto (browser-win-inst) (.setLocationBarVisible true))]
        (is (true? (config win :location-bar-visible?)))))))

(deftest browser-win-opt-menu-bar-visible?
  (testing "Setting the :menu-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isMenuBarVisisble (browser-win-inst))))
    (testing "shows the menu bar when provided a truthy value."
      (is (.isMenuBarVisisble (browser-win-inst :menu-bar-visible? true))))
    (testing "hides the menu bar when provided a falsey value."
      (let [win (browser-win-inst :menu-bar-visible? false)]
        (is (not (.isMenuBarVisisble win))))))
  (testing "Getting the :menu-bar-visible? option"
    (testing "returns the value of .isMenuBarVisisble"
      (let [win (doto (browser-win-inst) (.setMenuBarVisible false))]
        (is (false? (config win :menu-bar-visible?))))
      (let [win (doto (browser-win-inst) (.setMenuBarVisible true))]
        (is (true? (config win :menu-bar-visible?)))))))

(deftest browser-win-opt-status-bar-visible?
  (testing "Setting the :status-bar-visible? option"
    (testing "does nothing when omitted."
      (is (.isStatusBarVisisble (browser-win-inst))))
    (testing "shows the status bar when provided a truthy value."
      (is (.isStatusBarVisisble (browser-win-inst :status-bar-visible? true))))
    (testing "hides the status bar when provided a falsey value."
      (let [win (browser-win-inst :status-bar-visible? false)]
        (is (not (.isStatusBarVisisble win))))))
  (testing "Getting the :status-bar-visible? option"
    (testing "returns the value of .isStatusBarVisisble"
      (let [win (doto (browser-win-inst) (.setStatusBarVisible false))]
        (is (false? (config win :status-bar-visible?))))
      (let [win (doto (browser-win-inst) (.setStatusBarVisible true))]
        (is (true? (config win :status-bar-visible?)))))))

(deftest browser-win-opt-title
  (testing "Setting the :title option"
    (testing "does nothing when omitted."
      (is (= "" (.getTitle (browser-win-inst)))))
    (testing "changes the window's title when provided a value."
      (is (= "Foobar" (.getTitle (browser-win-inst :title "Foobar"))))))
  (testing "Getting the :title option returns the window's title."
    (let [win (doto (browser-win-inst) (.setTitle "Foobar"))]
      (is (= "Foobar" (config win :title))))))

(deftest browser-win-opt-size
  (testing "Setting the :size option"
    (testing "does nothing when omitted."
      (is (= (Dimension. 320 240) (.getSize (browser-win-inst)))))
    (testing "changes the window's size when provided a value."
      (let [size (Dimension. 640 480)]
        (is (= size (.getSize (browser-win-inst :size size)))))))
  (testing "Getting the :size option returns the window's size."
    (let [size (Dimension. 640 480)
          win  (doto (browser-win-inst) (.setSize size))]
      (is (= size (config win :size))))))
