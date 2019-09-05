package nl.numworx.swingbrowser.api;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class SwingBrowserProvider {



  public SwingBrowserProvider() {

  }

  public SwingBrowserFactory getFactory() throws NoSuchElementException {

    // try OSGI first
    try {
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      if (bundle != null) {
        BundleContext context = bundle.getBundleContext();
        ServiceReference<SwingBrowserFactory> reference =
            context.getServiceReference(SwingBrowserFactory.class);
        if (reference == null) throw new NoSuchElementException("Not found in OSGI");

        SwingBrowserFactory factory = context.getService(reference);
        if (factory != null) {
          // context.ungetService(reference); // wel of niet?
          return factory;
        }
        context.ungetService(reference); // wel of niet?
      }
    } catch (Exception ignore) {

    } catch (Error ignoretoo) {

    }

    return ServiceLoader.load(SwingBrowserFactory.class).iterator().next();
  }
}
