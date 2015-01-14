;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns ^{:doc "Holds functions for controlling the state of browser widgets of DJ
           Native Swing, e.g. for displaying and interacting with web content
           through them.

           See:
             http://djproject.sourceforge.net/ns/documentation/javadoc/chrriis/dj/nativeswing/swtimpl/components/JWebBrowser.html
           "
      :author "Mihail Ivanchev"}
  djnativeswing-clj.browser
  (:use [seesaw.event :only [listen-for-named-event]]
        [seesaw.util :only [illegal-argument]])
  (:import [chrriis.dj.nativeswing.swtimpl.components
            JWebBrowser
            JWebBrowser$WebBrowserDecoratorFactory
            WebBrowserFunction
            WebBrowserAdapter
            WebBrowserNavigationParameters]
           java.net.URL))

;;;; Event handling
;;;;

;;; For registering custom event types seesaw provides the
;;; listen-for-named-event multimethod. In the following, the browser specific
;;; events are made available using this mechanism.
;;;

(def ^{:private true} event-name-map
  {:command-received          'commandReceived
   :loading-progress          'loadingProgress
   :status-change             'statusChanged
   :title-change              'titleChanged
   :location-change-cancelled 'locationChangeCanceled
   :location-changing         'locationChanging
   :location-change           'locationChanged
   :window-closing            'windowClosing
   :window-opening            'windowOpening
   :window-will-open          'windowWillOpen})

(defmacro ^{:private true} add-listener-for-named-events
  [target events event-fn]
  `(let [listener# (proxy [WebBrowserAdapter] []
                     ~@(for [event events]
                         `(~(event-name-map event) [e#] (~event-fn e#))))]
     (.addWebBrowserListener ~target listener#)
     (fn [] (.removeWebBrowserListener ~target listener#))))

(defmethod listen-for-named-event
  [JWebBrowser :command-received]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:command-received] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :browser]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events
    this
    [:loading-progress
     :status-change
     :title-change
     :window-closing]
    event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :loading-progress]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:loading-progress] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :status-change]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:status-change] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :title-change]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:title-change] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :location]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events
    this
    [:location-change-cancelled
     :location-changing
     :location-change]
    event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :location-change-cancelled]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:location-change-cancelled] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :location-changing]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:location-changing] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :location-change]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:location-change] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :window-closing]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:window-closing] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :window-opening]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:window-opening] event-fn))

(defmethod listen-for-named-event
  [JWebBrowser :window-will-open]
  [^JWebBrowser this _ event-fn]
  (add-listener-for-named-events this [:window-will-open] event-fn))

;;;; Browser API
;;;;

(defn load!
  "Sends an HTTP GET or POST request to a given location asynchronously to load
  a resource or to transmit data.

  A POST request is made when a non-nil POST data is provided via the :post-data
  option. It can be a string or a map of string keys to string values. The
  browser displays the server's response.

  Without POST data, a GET request is made to load a resource. Generally prefer
  setting the widget's :url or :html properties to using load! for fetching
  resources.

  In both cases the location is either an instance of java.net.URL or a string
  holding the URL. Use the browser events and the widget's :progress property
  to keep track of the state.

  HTTP headers are controlled via the :headers option which accepts a map of
  string keys to string values.

  Returns the browser widget.

  Examples:

    (load! browser (java.net.URL. \"http://www.google.com\"))
    (load! browser \"http://localhost:8080\")
    (load! browser \"http://127.0.0.1\")
    (load!
      browser
      \"http://www.google.com\"
      :headers {\"user-agent\" \"hidden\"})
    (load!
      browser
      \"http://localhost/register.php\"
      :post-data {\"username\" \"john-doe\" \"password\" \"1234\"})
    (load!
      browser
      \"http://localhost/upload.php\"
      :post-data \"base64-encoded-image\")

  See:
    (reload!)
  "
  [^JWebBrowser target dest & {:keys [headers post-data]}]
  (let [url (cond
              (instance? URL dest) (str dest)
              (string? dest) dest
              :else (illegal-argument
                      "Invalid destination specified: %s"
                      dest))]
    (if (or headers post-data)
      (let [params (doto (WebBrowserNavigationParameters.)
                     (.setHeaders headers)
                     (.setPostData post-data))]
        (.navigate target url params))
      (.navigate target url)))
  target)

(defn reload!
  "Fully reloads the currently displayed resource, if there is one.

  A full reload implies that the content is re-requested from the server. As
  with load!, the browser events as well as the :progress property provide
  information to the operation's status.

  Returns the browser widget.

  See:
    (load!)
  "
  [^JWebBrowser target]
  (.reloadPage target)
  target)

(defn go-back!
  "Displays the page directly preceeding the current page in the browser
  history, if one is available.

  Returns the browser widget.

  See:
    (go-forward!)
  "
  [^JWebBrowser target]
  (.navigateBack target)
  target)

(defn go-forward!
  "Displays the page directly following the current page in the browser history,
  if one is available.

  Returns the browser widget.

  See:
    (go-back!)
  "
  [^JWebBrowser target]
  (.navigateForward target)
  target)

(defn stop-loading!
  "Aborts the loading of the currently requested resource.

  Returns the browser widget.

  See:
    (load!)"
  [^JWebBrowser target]
  (.stopLoading target)
  target)

(defn info
  "Returns miscellaneous information about the browser runtime of the spefied
  browser widget as a map currently consisting of the following keys:

    :type    a string with the type of the browser's runtime
    :version a string with the version of the browser or nil
  "
  [^JWebBrowser target]
  {:type (.getBrowserType target)
   :version (.getBrowserVersion target)})

(defn dispose!
  "Disposes the native SWT peer of the browser widget and optionally shows a
  dialog box to ask the user for confirmation if with-confirm is provided
  and equals true.

  Returns true if the native peer was successfully disposed, false otherwise."
  ([^JWebBrowser target]
    (dispose! target false))
  ([^JWebBrowser target with-confirm]
    (.disposeNativePeer target with-confirm)))

(defn print!
  "Prints the currently displayed content.

  The print dialog is shown for the user to adjust the print settings if
  with-dialog is true. Returns true if the operation is successful, false
  otherwise."
  [^JWebBrowser target ^Boolean with-dialog]
  (.print target with-dialog))

(defn exec-js!
  "Executes a chunk of JavaScript code within the context of the currently
  loaded resource.

  If with-result is true, the chunk should end with a return statement the value
  of which is the result of calling this function. If with-result is false or
  omitted, the function returns nil.

  Examples:

    (exec-js! browser \"alert(document.title);\")
    (exec-js! browser \"return 1 + 1;\") ; Result is ignored.
    (exec-js! browser \"alert(document.title); return document.title;\" true)
  "
  ([^JWebBrowser target js]
    (exec-js! target js false))
  ([^JWebBrowser target js with-result]
    (if with-result
      (.executeJavascriptWithResult target js)
      (.executeJavascript target js))))

(defn add-js-callback!
  "Registers a Clojure function as a JavaScript callback function with the
  specified name.

  Calling the callback function from JavaScript results in the invocation of the
  Clojure function with the passed parameters. The Clojure function is of the
  type (fn [target name & args] ...).

  Returns a function which removes the callback.

  Example:

    (defn js-callback
      [browser _ & [n s]]
      (println \"In Clojure!\")
      (println \"Number was:\" n)
      (println \"String was:\" s))

    (add-js-callback! browser \"foo\" js-callback)
    (exec-js! browser \"foo(1, \\\"Hello, World!\\\");\")
  "
  [^JWebBrowser target name f]
  (let [callback (proxy [WebBrowserFunction] [name]
                   (invoke [target args] (apply f target name args)))]
    (.registerFunction target callback)
    (fn [] (.unregisterFunction target callback)))
  target)

(def ^{:private true} cookie-attr-map
  {:domain ["Domain" true]
   :path ["Path" true]
   :expires ["Expires" true]
   :max-age ["Max-Age" true]
   :secure ["Secure" false]
   :http-only ["HttpOnly" false]})

(defn ^{:private true} cookie-attr
  [attr]
  (let [k (if (keyword? attr) attr (first attr))]
    (if-let [[k has-arg?] (cookie-attr-map k)]
      (if has-arg?
        (str ";" k "=" (nth 2 attr))
        (str ";" k))
      (throw (str "'" k "' is not a valid cookie attribute.")))))

(defn cookie
  "Returns the cookie with the specified name for the specified URL or nil if no
  matching cookie is available.

  See:
    (cookie!)
    (clear-cookies!)
  "
  [url name]
  (JWebBrowser/getCookie url name))

(defn cookie!
  "Stores a cookie for the specified URL with the given name and attributes.

  The attributes are passed as a sequence of k or [k v] where k is the
  attribute's name as a keyword and v is its value if there is one.

  Currently supported attributes are:

    :domain     <string>
    :path       <string>
    :expires    <string>
    :max-age    <string> | <number>
    :secure
    :http-only

  Returns nil.

  Examples:

    (cookie! \"http://www.yahoo.com\" \"foo\" \"bar\")
    (cookie!
      \"http://www.google.com\"
      \"foo\"
      \"bar\"
      [:secure [:max-age 6000]])

  See:
    (cookie)
    (clear-cookies!)
  "
  [url name val & attrs]
  (let [cookie (apply str name "=" val (map cookie-attr attrs))]
    (JWebBrowser/setCookie url cookie)))

(defn clear-cookies!
  "Deletes all stored cookies for all browser widgets and returns nil.

  See:
    (cookie)
    (cookie!)
  "
  []
  (JWebBrowser/clearSessionCookies))

(defn copy-appearance!
  "Adjusts the appearance of target (toolbars, status bar etc.) to match that of
  source and returns target."
  [source target]
  (JWebBrowser/copyAppearance source target)
  target)

(defn copy-content!
  "Copies the content of source (the requested resource's URL or the loaded HTML)
  into target and returns target."
  [source target]
  (JWebBrowser/copyContent source target)
  target)

(defn- to-decorator-factory
  [factory]
  (cond
    (instance? JWebBrowser$WebBrowserDecoratorFactory factory) factory
    (fn? factory) (proxy [JWebBrowser$WebBrowserDecoratorFactory] []
                    (createWebBrowserDecorator [webBrowser rendering-component]
                      (factory webBrowser rendering-component)))
    :else (illegal-argument
            "Don't know how to make WebBrowserDecoratorFactory from: %s"
            factory)))

(defn set-decorator-factory!
  "Sets a default decorator factory function to be used for new browser widgets
  and browser popup windows if nothing else is specified, or removes a
  previously set decorator factory when f is equal to nil in which case a
  default factory is used internally.

  The purpose of the decorator is to provide common controls for interacting
  with a browser such as a navigation panel, status bar, menus and so on.

  The decorator factory is either an instance of
  JWebBrowser$WebBrowserDecoratorFactory or a function of type
  (fn [browser rendering-component] ...) which returns an instance of
  WebBrowserDecorator.

  An easily extensible default decorator is provided by the class
  DefaultWebBrowserDecorator which is also the default decorator used by the
  browser widgets.

  Returns nil.

  Examples:

    (set-decorator-factory!
      (fn [browser rendering-component]
        (proxy [DefaultWebBrowserDecorator] [browser rendering-component]
          (addMenuBarComponents [menu-bar]
            (proxy-super addMenuBarComponents menu-bar)
              (let [items [(sw/menu-item :text \"My Custom Item 1\")
                           (sw/menu-item :text \"My Custom Item 2\")]
                    menu  (sw/menu :text \"[[My Custom Menu]]\" :items items)]
                (sw/add! menu-bar menu))))))

  See:
    chrriis.dj.nativeswing.swtimpl.components.JWebBrowser$WebBrowserDecoratorFactory
    chrriis.dj.nativeswing.swtimpl.components.WebBrowserDecorator
    djnativeswing_clj.DefaultWebBrowserDecorator
  "
  [f]
  (JWebBrowser/setWebBrowserDecoratorFactory (when f (to-decorator-factory f))))
