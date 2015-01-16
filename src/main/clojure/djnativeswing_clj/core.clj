;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns ^{:doc "The core namespace of the DJ Native Swing wrapper of seesaw. Holds functions
           for creating and manipulating DJ Native Swing widgets. "
      :author "Mihail Ivanchev"}
  djnativeswing-clj.core
  (:use [seesaw.core :only [base-resource-options construct default-options]]
        [seesaw.config :only [Configurable]]
        [seesaw.options :only [apply-options
                               bean-option
                               default-option
                               get-option-value
                               option-map
                               option-provider
                               resource-option]]
        [seesaw.util :only [illegal-argument]]
        [seesaw.widget-options :only [widget-option-provider]])
  (:require [djnativeswing-clj.browser :as browser]
            [djnativeswing-clj.native-interface :as ni])
  (:import  [chrriis.dj.nativeswing.swtimpl.components
             Credentials
             JWebBrowser
             JWebBrowser$WebBrowserDecoratorFactory
             JWebBrowserWindow
             WebBrowserAdapter
             WebBrowserAuthenticationHandler
             WebBrowserWindowFactory]
            chrriis.dj.nativeswing.swtimpl.NativeInterface
            chrriis.dj.nativeswing.NSOption
            djnativeswing_clj.JCustomWebBrowser
            java.awt.Window))

;;;; Aliases
;;;;

; alias native-interface/open! for convenience
(def ^{:doc (str "Alias of djnativeswing-clj.native-interface/open!:\n" (:doc (meta #'ni/open!)))} open-native-interface! ni/open!)

; alias native-interface/close! for convenience
(def ^{:doc (str "Alias of djnativeswing-clj.native-interface/close!:\n" (:doc (meta #'ni/close!)))} close-native-interface! ni/close!)

; alias native-interface/run-event-pump! for convenience
(def ^{:doc (str "Alias of djnativeswing-clj.native-interface/run-event-pump!:\n" (:doc (meta #'ni/run-event-pump!)))} run-event-pump! ni/run-event-pump!)

;;;; Browser
;;;;

(defn- default-authentication-handler
  [f]
  (proxy [WebBrowserAuthenticationHandler] []
    (getCredentials [browser resource-location]
      (when-let [[usr pwd] (f browser resource-location)]
        (Credentials. usr pwd)))))

(defn- ^WebBrowserAuthenticationHandler to-authentication-handler
  [h]
  (cond
    (instance? WebBrowserAuthenticationHandler h) h
    (fn? h) (default-authentication-handler h)
    :else (illegal-argument
            "Don't know how to make WebBrowserAuthenticationHandler from: %s"
            h)))

(def ^{:private true} browser-options
  (merge
    default-options
    (option-map
      (resource-option :resource base-resource-options)
      (default-option
        :bars-visible?
        #(.setBarsVisible ^JWebBrowser %1 (boolean %2)))
      (default-option
        :title
        #(illegal-argument "No setter defined for option title")
        #(.getPageTitle ^JWebBrowser %1))
      (bean-option
        :authentication-handler
        JWebBrowser
        to-authentication-handler)
      (bean-option :button-bar-visible? JWebBrowser boolean)
      (default-option
        :default-popup-menu-registered?
        #(.setDefaultPopupMenuRegistered ^JWebBrowser %1 (boolean %2)))
      (bean-option [:html :h-t-m-l-content] JWebBrowser str)
      (bean-option [:js-enabled? :javascript-enabled?] JWebBrowser boolean)
      (bean-option :location-bar-visible? JWebBrowser boolean)
      (bean-option :menu-bar-visible? JWebBrowser boolean)
      (bean-option :status-bar-visible? JWebBrowser boolean)
      (default-option
        :back-nav-enabled?
        #(illegal-argument "No setter defined for option back-nav-enabled?")
        #(.isBackNavigationEnabled ^JWebBrowser %1))
      (default-option
        :forward-nav-enabled?
        #(illegal-argument "No setter defined for option forward-nav-enabled?")
        #(.isForwardNavigationEnabled ^JWebBrowser %1))
      (default-option
        :progress
        #(illegal-argument "No setter defined for option process")
        #(.getLoadingProgress ^JWebBrowser %1)))))

(widget-option-provider JWebBrowser browser-options)

(def ^{:private true} browser-runtime-map
  {:xulrunner (JWebBrowser/useXULRunnerRuntime)
   :webkit    (JWebBrowser/useWebkitRuntime)})

(def ^{:private true} to-decorator-factory #'browser/to-decorator-factory)

(defn browser
  "Creates a web browser widget. It supports the following options in addition
  to the standard seesaw widget options.

    :bars-visible?          controls the visibility of the browser bars
    :button-bar-visible?    controls/queries the visibility of the button bar
    :menu-bar-visible?      controls/queries the visibility of the menu bar
    :location-bar-visible?  controls/queries the visibility of the location bar
    :status-bar-visible?    controls/queries the visibility of the status bar
    :title                  queries the title of the web page
    :authentication-handler sets the authentication handler of the widget
    :html                   sets/queries the HTML content of the widget
    :url                    sets/queries the URL of the current resource
    :back-nav-enabled?      queries whether the back navigation is enabled
    :forward-nav-enabled?   queries whether the forward navigation is enabled
    :progress               queries the loading percentage of the resource

  The :authentication-handler option accepts either an instance of
  chrriis.dj.nativeswing.swtimpl.components.WebBrowserAuthenticationHandler or
  a function like (fn [browser location] [\"username\" \"password\"]) returning
  the user credentials for the protected location accessed by the browser widget
  or nil if the authentication is to be aborted.

  Additionally, when constructing widgets with this function you can also
  specify the following options:

    :runtime           the browser's runtime
    :url               the URL to be initially loaded by the widget
    :decorator-factory the decorator factory for the supplementary GUI
    :options           additional options for DJ Native Swing

  The :runtime option can take the values:

     :xulrunner the component will use the XULRunner runtime
     :webkit    the component will use the WebKit runtime

  To use XULRunner you need to install the XULRunner SDK (obtainable at
  https://developer.mozilla.org/en-US/docs/Gecko_SDK#Downloading) and specify
  the path to it. For this, you can either set the environment variable
  XULRUNNER_HOME or the system property nativeswing.webbrowser.xulrunner.home.

  See the documentation of the djnativeswing-clj.browser/load! and
  djnativeswing-clj.browser/set-decorator-factory! functions for the values
  accepted by the :url and :decorator-factory options respectively.

  To configure the widget's native backend, you can pass a sequence of
  chrriis.dj.nativeswing.NSOption instances through the :options option.

  Browser widget support the following events:

    :loading-progress
    :status-change
    :title-change
    :location                  a common listener for :location-xyz events
    :location-change-cancelled
    :location-changing
    :location-change
    :window-closing
    :window-opening
    :window-will-open

  See the documentation of the JWebBrowser class for the semantics of these events.

  The native interface must be opened via open-native-interface! before browser
  widgets are used.

  Examples:

    (browser)
    (browser :runtime :xulrunner :url \"http://www.google.com\")
    (browser :bars-visible? false
             :html \"<html><head><title>X</title></head><body></body>Y</html>\")

  See:
    (djnativeswing-clj.core/open-native-interface!)
    http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis.dj.nativeswing.swtimpl.components.JWebBrowser
    http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis.dj.nativeswing.NSOption
    http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis.dj.nativeswing.swtimpl.components.WebBrowserAuthenticationHandler
  "
  [& {:keys [runtime options url decorator-factory] :as opts}]
  (let [factory (when decorator-factory
                  (to-decorator-factory decorator-factory))]
    (set! JCustomWebBrowser/decoratorFactory factory)
    (let [options (cond-> options runtime (conj (browser-runtime-map runtime)))
          options (when (seq options)
                    (into-array NSOption options))
          browser (construct JCustomWebBrowser options)]
      (apply-options
        browser
        (dissoc opts :runtime :options :url :decorator-factory))
      (when url
        (browser/load! browser url))
      browser)))

;;;; Browser window
;;;;

(extend-protocol Configurable
  JWebBrowserWindow
  (config* [target name] (get-option-value target name))
  (config!* [target args] (apply-options target args)))

(def ^{:private true} browser-window-options
  (option-map
    (bean-option :title JWebBrowserWindow)
    (bean-option :size JWebBrowserWindow)
    (default-option :browser
      #(illegal-argument "No setter defined for option browser")
      #(.getWebBrowser ^JWebBrowserWindow %1))
    (default-option
      :button-bar-visible?
      #(.setButtonBarVisible ^JWebBrowserWindow %1 %2)
      #(.isButtonBarVisisble ^JWebBrowserWindow %1))
    (default-option
      :location-bar-visible?
      #(.setLocationBarVisible ^JWebBrowserWindow %1 %2)
      #(.isLocationBarVisisble ^JWebBrowserWindow %1))
    (default-option
      :menu-bar-visible?
      #(.setMenuBarVisible ^JWebBrowserWindow %1 %2)
      #(.isMenuBarVisisble ^JWebBrowserWindow %1))
    (default-option
      :status-bar-visible?
      #(.setStatusBarVisible ^JWebBrowserWindow %1 %2)
      #(.isStatusBarVisisble ^JWebBrowserWindow %1))
    (default-option
      :bars-visible?
      #(.setBarsVisible ^JWebBrowserWindow %1 (boolean %2)))
    (default-option :location #(.setLocation ^JWebBrowserWindow %1 %2))
    (default-option
      :icon-image
      #(.setIconImage ^JWebBrowserWindow %1 %2))
    (default-option :visible? #(.setVisible ^JWebBrowserWindow %1 %2))))

(option-provider JWebBrowserWindow browser-window-options)

(defn browser-window
  "Creates a new popup window for the specified browser widget. For example, a
  popup window is automatically created when the user clicks the \"Open in a new
  window\" context menu option. The window itself is not a widget, but is
  configurable as one and supports the following options:

    :browser                queries the browser widget of the window
    :visible                controls the visibility of the window
    :bars-visible?          controls the visibility of the browser bars
    :button-bar-visible?    controls/queries the visibility of the button bar
    :menu-bar-visible?      controls/queries the visibility of the menu bar
    :location-bar-visible?  controls/queries the visibility of the location bar
    :status-bar-visible?    controls/queries the visibility of the status bar
    :title                  controls/queries the title of the window's title
    :size                   controls/queries the dimensions of the window
    :location               controls the screen location of the window
    :icon-image             controls the icon image of the window

  Examples:

    (browser-window browser :visible true :bars-visible? false)

  See:
    http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis/dj/nativeswing/swtimpl/components/JWebBrowserWindow.html
  "
  [target & opts]
  (let [[parent browser opts] (if (instance? JWebBrowser target)
                                [nil target opts]
                                [target (first opts) (rest opts)])
        window                (WebBrowserWindowFactory/create parent browser)]
    (if (seq opts)
      (apply-options window (apply hash-map opts))
      window)))
