package nl.numworx.swingbrowser.jxb;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Activator implements BundleActivator {

  private ProviderFactory factory;

  @Override
  public void start(BundleContext context) throws Exception {
    factory = new ProviderFactory();
    context.registerService(SwingBrowserFactory.class.getName(), factory, new Hashtable<String,Object>());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    factory.close();
  }

}
