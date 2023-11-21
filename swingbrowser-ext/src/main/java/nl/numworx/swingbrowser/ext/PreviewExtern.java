package nl.numworx.swingbrowser.ext;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

public class PreviewExtern extends JPanel implements ServiceTrackerCustomizer<HttpService, HttpService>, Action, SwingBrowser, SCORM2004APIInterface {

	class PreviewContext implements HttpContext {

		@Override
		public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
			return true;
		}

		@Override
		public URL getResource(String name) {
			return context.getBundle().getResource(name); // zonder /
			//return getClass().getResource("/' + name); // met / aan het begin
		}

		@Override
		public String getMimeType(String name) {
			if (name.endsWith(".css")) {
				return "text/css";
			}
			if (name.endsWith(".js")) {
				return "text/javascript";
			}
			return null;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4949714665314985933L;

	BundleContext context;
	ServiceTracker<HttpService, HttpService> tracker;

	private Object port = 8080;

	private JButton btn;

    private SCORM2004APIInterface api;
		
	public String Initialize(String dummy) {
		if (api != null)
			return api.Initialize(dummy);
		return "true";
  }

	Hashtable<String, String> cmi = new Hashtable<>();

	private ServiceRegistration<SwingBrowserFactory> service;

	private Dictionary<String, Object> properties;
	
	void fire(String key, String value) {
		String old = cmi.put(key, value);
		firePropertyChange(key, old, value);
	}
	
  public String Commit(String dummy) {
	    if (api != null) 
	    	return api.Commit(dummy);
		firePropertyChange("Commit", dummy, null);
	    return "true";
  }

  public String Terminate(String dummy) {
	  if (api != null)
		return api.Terminate(dummy);
	  firePropertyChange("Terminate", dummy, null);
	  return "true";
  }

  public String GetValue(String key) {
    if (api != null)
      return api.GetValue(key);
    return cmi.getOrDefault(key, "");
  }

  public String SetValue(String key, String value) {
	if (api != null)
		return api.SetValue(key, value);
	fire(key, value);
	return "true";
  }

  public String GetLastError() {
    return api.GetLastError();
  }

  public String GetDiagnostic(String iErrorCode) {
    return api.GetDiagnostic(iErrorCode);
  }

  public String GetErrorString(String iErrorCode) {
    return api.GetErrorString(iErrorCode);
  }

  public PreviewExtern(BundleContext bundleContext, ServiceRegistration<SwingBrowserFactory> service, Dictionary<String,Object> properties) {
        context = bundleContext;
        this.service = service;
        this.properties = properties;
	}

	public void init() {
		tracker = new ServiceTracker<>(context, HttpService.class, this);
		putValue(NAME, "Open Browser");
		putValue(ACTION_COMMAND_KEY, "about:blank");
		btn = new JButton(this);
		btn.setEnabled(false);
		add(btn);
		setOpaque(true);
		setBackground(Constants.COLOR10);
		
	}

	public void start() {
		tracker.open();
	}
	
	public void close() {
		tracker.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
		        initService(tracker.getService());
		    	String action = e.getActionCommand();
				String path = getPath(action);
		    	if (api != null) path = "/local/";
				Desktop.getDesktop().browse(URI.create("http://127.0.0.1:" + port + path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	private String getPath(String action) {
		URI uri = URI.create(action);
		String path = uri.getRawPath();
		if (path == null) path = "/";
		String q = uri.getRawQuery();
		String h = uri.getRawFragment();
		if (q != null) path += "?" + q;
		if (h != null) path += "#" + h;
		return path;
	}

	@Override
	public HttpService addingService(ServiceReference<HttpService> reference) {
		port = reference.getProperty("org.osgi.service.http.port");
		if (port == null) port = reference.getProperty("http.port");
		properties.put("org.osgi.service.http.port", port);
		this.service.setProperties(properties);
		HttpService service = context.getService(reference);
		btn.setEnabled(true);
		return service;
	}

  private void initService(HttpService service) {
    Dictionary<String,String> initparams = new Hashtable<>();
		initparams.put("url", getValue(ACTION_COMMAND_KEY).toString());
		initparams.put("local", "http://127.0.0.1:" + port + getPath(getValue(ACTION_COMMAND_KEY).toString()));
		HttpContext ctx = createHttpContext();
		try {
			service.registerServlet("/", new PreviewServlet(), initparams, ctx);
			service.registerServlet("/local", new LocalServlet(this), initparams, ctx);
			service.registerResources("/local/resources", "nl/numworx/swingbrowser/ext/resources", ctx);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
  }

	private HttpContext createHttpContext() {
	return new PreviewContext();
}

	@Override
	public void modifiedService(ServiceReference<HttpService> reference, HttpService service) {
		port = reference.getProperty("org.osgi.service.http.port");
	}

	@Override
	public void removedService(ServiceReference<HttpService> reference, HttpService service) {
		btn.setEnabled(false);
		service.unregister("/");
		service.unregister("/local/resources");
		context.ungetService(reference);
	}

  @Override
  public JComponent asComponent() {
    return this;
  }

  @Override
  public void loadContent(String content, String type) {
	putValue(ACTION_COMMAND_KEY, "about:blank");  
    putValue(SHORT_DESCRIPTION, null);
    putValue(NAME, "Open " + type);
  }

  @Override
  public void loadURL(String url) {
	putValue(ACTION_COMMAND_KEY, url);  
	putValue(SHORT_DESCRIPTION, url);
	putValue(NAME, "Open URL");
  }

  @Override
  public void setAPI(SCORM2004APIInterface api) {
    this.api = api;
    
  }

  @Override
  public void addTitleListener(TitleListener l) {
  }

  @Override
  public void removeTitleListener(TitleListener l) {
  }

  @Override
  public void addRefreshListener(RefreshListener l) {
  }

  @Override
  public void removeRefreshListener(RefreshListener l) {
  }

  @Override
  public void addStatusListener(StatusListener l) {
  }

  @Override
  public void removeStatusListener(StatusListener l) {
  }

  @Override
  public void addConsoleListener(ConsoleListener l) {
  }

  @Override
  public void removeConsoleListener(ConsoleListener l) {
  }

	@Override
	public void setName(String name) {
		putValue(NAME, name);
	}

	private Map<String,Object> values = new HashMap<>();
	
	@Override
	public Object getValue(String key) {
		return values.get(key);
	}

	@Override
	public void putValue(String key, Object value) {
		Object old = values.put(key, value);
		firePropertyChange(key, old, value);
	}

}
