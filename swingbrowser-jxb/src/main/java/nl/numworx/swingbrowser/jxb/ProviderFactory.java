package nl.numworx.swingbrowser.jxb;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class ProviderFactory implements ServiceFactory<JXBFactory> {

  public ProviderFactory() {
  }

  @Override
  public JXBFactory getService(Bundle bundle, ServiceRegistration<JXBFactory> registration) {
    JXBFactory f = new PooledJXBFactory();
    return f;
  }

  @Override
  public void ungetService(Bundle bundle, ServiceRegistration<JXBFactory> registration,
      JXBFactory service) {
    service.close();    
  }

  public void close() {
  }

}
