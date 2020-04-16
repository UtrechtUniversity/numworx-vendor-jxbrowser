package nl.numworx.swingbrowser.ext;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Activator implements BundleActivator, SwingBrowserFactory {

  private BundleContext context;
  private Hashtable<String, Object> properties = new Hashtable<>();
  private ServiceRegistration<SwingBrowserFactory> service;
  
  @Override
  public void start(BundleContext context) throws Exception {
    this.context = context;
    properties.put("nl.numworx.swingbrowser.type", "ext");
	properties.put(Constants.SERVICE_RANKING, -100);
	properties.put("org.osgi.service.http.port",0);
	service = context.registerService(SwingBrowserFactory.class, this, properties);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }

  @Override
  public SwingBrowser newBrowser() {
    PreviewExtern preview = new PreviewExtern(context, service, properties);
    preview.init();
    preview.start();
    return preview;
  }

  @Override
  public void newSession() {
    // nog uitzoeken.    
  }

}
