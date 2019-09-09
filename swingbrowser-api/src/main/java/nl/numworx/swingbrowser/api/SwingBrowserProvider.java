package nl.numworx.swingbrowser.api;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class SwingBrowserProvider {

  private final Logger LOG = Logger.getLogger(getClass().getName());
  private SwingBrowserFactory factory;
  
  public SwingBrowserFactory getFactory() throws NoSuchElementException {

    if (factory != null) {
      return factory;
    }
      
    // try OSGI first
    try {
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      if (bundle != null) {
        if (bundle.getState() != Bundle.ACTIVE)
          bundle.start();
        BundleContext context = bundle.getBundleContext(); // context null: not started yet.
        
        ServiceReference<SwingBrowserFactory> reference =
            context.getServiceReference(SwingBrowserFactory.class);
        if (reference == null) throw new NoSuchElementException("Not found in OSGI");

        SwingBrowserFactory factory = context.getService(reference);
        if (factory != null) {
          // context.ungetService(reference); // wel of niet?
          return this.factory = factory;
        }
        context.ungetService(reference); // wel of niet?
      }
    } catch(NoClassDefFoundError noclass) {
      // common case.
    } catch (Exception ignore) {
      LOG.log(Level.WARNING, "getFactory osgi", ignore);
    } catch (Error ignoretoo) {
      LOG.log(Level.WARNING, "getFactory osgi", ignoretoo);
    }
    return this.factory = ServiceLoader.load(SwingBrowserFactory.class, getClass().getClassLoader()).iterator().next();
  }
}
