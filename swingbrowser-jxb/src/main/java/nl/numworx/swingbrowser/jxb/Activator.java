package nl.numworx.swingbrowser.jxb;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.provisioning.ProvisioningService;

import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Activator implements BundleActivator {

  private ProviderFactory factory;

  @Override
  public void start(BundleContext context) throws Exception {
	ServiceReference<ProvisioningService> reference = context.getServiceReference(ProvisioningService.class);
	if (reference != null) {
		ProvisioningService service = context.getService(reference);
		if (service != null) {
			Object value = service.getInformation().get(JXBFactory.JXBROWSER_LICENSE_KEY);
			if (value != null) {
				System.setProperty(JXBFactory.JXBROWSER_LICENSE_KEY, value.toString());
			}
		}		
		context.ungetService(reference);
	}
	  
    factory = new ProviderFactory();
    context.registerService(SwingBrowserFactory.class.getName(), factory, new Hashtable<String,Object>());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    factory.close();
  }

}
