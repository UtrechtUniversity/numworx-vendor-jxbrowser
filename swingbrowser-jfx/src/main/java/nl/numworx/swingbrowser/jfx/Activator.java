package nl.numworx.swingbrowser.jfx;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Activator implements BundleActivator {

  @Override
  public void start(BundleContext context) throws Exception {
    context.registerService(SwingBrowserFactory.class, new JFXFactory(), new Hashtable<String,Object>());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }

}
