package nl.numworx.swingbrowser.jfx;

import java.net.CookieHandler;
import java.net.CookieManager;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JFXFactory implements SwingBrowserFactory {

  static {
    CookieHandler.setDefault(new CookieManager());
  }
  
  public JFXFactory() throws ClassNotFoundException {
    Class.forName("javafx.scene.web.WebView");
  }

  @Override
  public SwingBrowser newBrowser() {
    return new JFXBrowser();
  }

  @Override
  public void newSession() {
    CookieHandler handler = CookieHandler.getDefault();
    if (handler instanceof CookieManager) {
      CookieManager manager = (CookieManager) handler;
      manager.getCookieStore().removeAll();
    }
    
  }

}
