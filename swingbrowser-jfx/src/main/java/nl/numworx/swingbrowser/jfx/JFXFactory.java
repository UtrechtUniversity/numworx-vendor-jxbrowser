package nl.numworx.swingbrowser.jfx;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JFXFactory implements SwingBrowserFactory {

  public JFXFactory() {
  }

  @Override
  public SwingBrowser newBrowser() {
    // TODO Auto-generated method stub
    return new JFXBrowser();
  }

}
