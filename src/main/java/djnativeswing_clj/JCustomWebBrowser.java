/*
 * Copyright (c) Mihail Ivanchev, 2014. All rights reserved.
 *
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */

package djnativeswing_clj;

import java.awt.Component;
import java.lang.reflect.Method;

import chrriis.dj.nativeswing.NSOption;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserDecorator;

/**
 * Extends JWebBrowser to provide a way to specify a decorator factory to be
 * used only for the next instance of the class.
 */
public class JCustomWebBrowser extends JWebBrowser {

  private static final long serialVersionUID = 1L;

  /**
   * Holds the factory to be used to construct the decorator of the next
   * instance of this class.
   *
   * If this field is not null upon the next initialization of this class,
   * the factory will be used to obtain a decorator for the web browser and
   * the value of this field will be set to null.
   */
  public static WebBrowserDecoratorFactory decoratorFactory;

  private WebBrowserDecorator webBrowserDecorator;

  public JCustomWebBrowser(NSOption ...options) {
    super(options);
  }

  @Override
  protected WebBrowserDecorator createWebBrowserDecorator(Component renderingComponent) {
    if (decoratorFactory != null) {
      webBrowserDecorator = decoratorFactory.createWebBrowserDecorator(this, renderingComponent);
      decoratorFactory = null;
    }
    else {
      webBrowserDecorator = super.createWebBrowserDecorator(renderingComponent);
    }
    return webBrowserDecorator;
  }
}
