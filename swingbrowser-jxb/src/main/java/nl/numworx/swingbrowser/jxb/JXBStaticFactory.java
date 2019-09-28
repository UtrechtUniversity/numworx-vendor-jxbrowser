package nl.numworx.swingbrowser.jxb;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JXBStaticFactory implements SwingBrowserFactory {

    static private JXBFactory factory; // the one and only

    @Override
    public SwingBrowser newBrowser() {
      if (factory == null) {
        synchronized(getClass()) {
          if (factory == null) {
            factory  = new JXBFactory();
          }
        }
      }
      return factory.newBrowser();
    }
    
  
}
