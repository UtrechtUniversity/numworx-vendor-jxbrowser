package nl.numworx.swingbrowser.ext;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.JFrame;

import org.apache.felix.connect.launch.ClasspathScanner;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.apache.felix.connect.launch.PojoServiceRegistryFactory;
import org.osgi.framework.ServiceReference;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class Main {

  public static void main(String[] args) throws Exception {
    PojoServiceRegistryFactory factory = ServiceLoader.load(PojoServiceRegistryFactory.class).iterator().next();
    Map<String, Object> map = new HashMap<String,Object>();
    map.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles());
    System.setProperty("org.osgi.service.http.port", "8686");
    PojoServiceRegistry framework = factory.newPojoServiceRegistry(map);
    JFrame frame = new JFrame("preview extern");
    new Activator().start(framework.getBundleContext());
//    PreviewExtern applet = new PreviewExtern(framework.getBundleContext());
//    applet.init();
//    applet.start();swing
    ServiceReference<?> ref = framework.getServiceReference(SwingBrowserFactory.class.getName());
    SwingBrowserFactory fac = (SwingBrowserFactory) framework.getService(ref);
    SwingBrowser applet = fac.newBrowser();
    frame.setContentPane(applet.asComponent());
    applet.loadURL("https://app.dwo.nl/dwo/apps/player.html#cmi.launch_data:507040");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.show();
  }

}
