package nl.numworx.swingbrowser.jxb;

import java.util.NoSuchElementException;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JXBStaticFactory implements SwingBrowserFactory {

    static volatile private JXBFactory factory; // the one and only

    @Override
    public SwingBrowser newBrowser() {
      if (factory == null) {
        synchronized(getClass()) {
          if (factory == null) {
            factory  = new PooledJXBFactory();
          }
        }
      }
      return factory.newBrowser();
    }
    
    public JXBStaticFactory() throws NoSuchElementException, ClassNotFoundException {
          Class.forName("com.teamdev.jxbrowser.engine.Engine");
    }
}
