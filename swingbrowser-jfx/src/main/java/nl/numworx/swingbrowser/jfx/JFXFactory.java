package nl.numworx.swingbrowser.jfx;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JFXFactory implements SwingBrowserFactory {

  public JFXFactory() throws ClassNotFoundException {
    Class.forName("javafx.scene.web.WebView");
  }

  @Override
  public SwingBrowser newBrowser() {
    return new JFXBrowser();
  }

}
