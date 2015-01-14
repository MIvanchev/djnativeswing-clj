;; Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns djnativeswing-clj.native-interface-test
  (:use clojure.test
        djnativeswing-clj.native-interface
        [seesaw.core :only [invoke-now]])
  (:import chrriis.dj.nativeswing.swtimpl.NativeInterface))

(defn native-fixture
  [f]
  (f)
  (when (NativeInterface/isOpen)
    (NativeInterface/close)))

(use-fixtures :each native-fixture)

(deftest initialize!-test
  (testing "initialize! initializes the native interface."
    (initialize!)
    (is (NativeInterface/isInitialized))))

;(deftest initialized?-test
;  (testing "initialized? returns true iff the native interface is initialized."
;    (is (not (initialized?)))
;    (NativeInterface/initialize)
;    (is (initialized?))))

(deftest open!-test
  (testing "open! opens the native interface."
    (open!)
    (is (NativeInterface/isOpen))))

(deftest open?-test
  (testing "open? returns true iff the native interface is open."
    (is (not (open?)))
    (NativeInterface/open)
    (is (open?))))

(deftest ui-thread?-test
  (testing "(ui-thread? false) returns true iff evaluated on the EDT."
    (open!)
    (is (not (ui-thread? false)))
    (is (invoke-now (ui-thread? false)))))
