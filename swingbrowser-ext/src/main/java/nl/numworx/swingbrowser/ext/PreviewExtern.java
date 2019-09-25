package nl.numworx.swingbrowser.ext;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

public class PreviewExtern extends JPanel implements ServiceTrackerCustomizer<HttpService, HttpService>, ActionListener, SwingBrowser, SCORM2004APIInterface {

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
    return api.Initialize(dummy);
  }

  public String Commit(String dummy) {
    return api.Commit(dummy);
  }

  public String Terminate(String dummy) {
    return api.Terminate(dummy);
  }

  public String GetValue(String key) {
    if (api != null)
      return api.GetValue(key);
    return "";
  }

  public String SetValue(String key, String value) {
    return api.SetValue(key, value);
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

  public PreviewExtern(BundleContext bundleContext) {
        context = bundleContext;
	}

	public void init() {
		tracker = new ServiceTracker<>(context, HttpService.class, this);
		btn = new JButton("Open Browser");
		btn.setActionCommand("about:blank");
		btn.setEnabled(false);
		add(btn);
		btn.addActionListener(this);
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
		    	path = "/local/";
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
		HttpService service = context.getService(reference);
		btn.setEnabled(true);
		return service;
	}

  private void initService(HttpService service) {
    Dictionary<String,String> initparams = new Hashtable<>();
		initparams.put("url", btn.getActionCommand());
		initparams.put("local", "http://127.0.0.1:" + port + getPath(btn.getActionCommand()));
		HttpContext ctx = service.createDefaultHttpContext();
		try {
			service.registerServlet("/", new PreviewServlet(), initparams, ctx);
			service.registerServlet("/local", new LocalServlet(this), initparams, ctx);
			service.registerResources("/local/resources", "/nl/numworx/swingbrowser/ext/resources", ctx);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
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
    btn.setActionCommand("about:blank");
    btn.setToolTipText(null);
    btn.setText("Open " + type);
  }

  @Override
  public void loadURL(String url) {
    btn.setActionCommand(url);
    btn.setToolTipText(url);
    btn.setText("Open URL");
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

}
