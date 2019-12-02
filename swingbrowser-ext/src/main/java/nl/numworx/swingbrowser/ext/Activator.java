package nl.numworx.swingbrowser.ext;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Activator implements BundleActivator, SwingBrowserFactory {

  private BundleContext context;
  @Override
  public void start(BundleContext context) throws Exception {
    this.context = context;
    context.registerService(SwingBrowserFactory.class, this, new Hashtable<>());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }

  @Override
  public SwingBrowser newBrowser() {
    PreviewExtern preview = new PreviewExtern(context);
    preview.init();
    preview.start();
    return preview;
  }

  @Override
  public void newSession() {
    // nog uitzoeken.    
  }

}
