package nl.numworx.swingbrowser.ext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.JFrame;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

public class Main {

  public static class API implements SCORM2004APIInterface, PropertyChangeListener {

		@Override
		public String Initialize(String dummy) {
			return "true";
		}

		@Override
		public String Commit(String dummy) {
			return "true";
		}

		@Override
		public String Terminate(String dummy) {
			System.exit(0);
			return "true";
		}

		@Override
		public String GetValue(String key) {
			// TODO Auto-generated method stub
			return "";
		}

		@Override
		public String SetValue(String key, String value) {
			// TODO Auto-generated method stub
			return "true";
		}

		@Override
		public String GetLastError() {
			return "0";
		}

		@Override
		public String GetDiagnostic(String iErrorCode) {
			return "";
		}

		@Override
		public String GetErrorString(String iErrorCode) {
			return "no error";
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("Terminate".equals(evt.getPropertyName()))
				Terminate((String) evt.getOldValue());
			else if (evt.getNewValue() instanceof String)
				SetValue(evt.getPropertyName(), (String) evt.getNewValue());			
		}

	}

public static void main(String[] args) throws Exception {
	FrameworkFactory factory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
    Map<String, String> map = new HashMap<>();
    System.setProperty("org.osgi.service.http.port", "8686");
    System.setProperty("felix.log.level", "4");
    Framework framework = factory.newFramework(map);
    framework.init();
    framework.start();
    JFrame frame = new JFrame("preview extern");
    new Activator().start(framework.getBundleContext());

    ServiceReference<SwingBrowserFactory> ref = framework.getBundleContext().getServiceReference(SwingBrowserFactory.class);
    SwingBrowserFactory fac = framework.getBundleContext().getService(ref);
    SwingBrowser applet = fac.newBrowser();
    API api = new API();
//	applet.setAPI(api);
    frame.setContentPane(applet.asComponent());
//    applet.loadURL("https://app.dwo.nl/dwo/apps/player.html#641855");
    applet.loadURL("https://numworx.uu.nl/dwo/saml/login.jsp?r=8686"); // moet naar "_top" red
    applet.asComponent().setName("Please login");
    applet.asComponent().addPropertyChangeListener(api);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.show();
  }

}
