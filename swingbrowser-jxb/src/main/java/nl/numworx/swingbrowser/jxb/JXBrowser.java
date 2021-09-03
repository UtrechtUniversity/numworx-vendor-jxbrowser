package nl.numworx.swingbrowser.jxb;

import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.browser.event.ConsoleMessageReceived;
import com.teamdev.jxbrowser.browser.event.StatusChanged;
import com.teamdev.jxbrowser.browser.event.TitleChanged;
import com.teamdev.jxbrowser.cookie.Cookie;
import com.teamdev.jxbrowser.cookie.CookieStore;
import com.teamdev.jxbrowser.event.Observer;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.frame.LoadDataParams;
import com.teamdev.jxbrowser.js.ConsoleMessageLevel;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.navigation.event.NavigationRedirected;
import com.teamdev.jxbrowser.navigation.event.NavigationStarted;
import com.teamdev.jxbrowser.net.MimeType;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import nl.numworx.swingbrowser.api.ConsoleEvent;
import nl.numworx.swingbrowser.api.ConsoleEvent.Level;
import nl.numworx.swingbrowser.api.StatusEvent;
import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.TitleEvent;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

public class JXBrowser implements SwingBrowser, Runnable, ConsoleListener {

  private volatile TitleListener title;
  private volatile RefreshListener refresh;
  private volatile StatusListener status;
  private volatile ConsoleListener console = this;
  private final JXBFactory jxb;
  private API stub;

  Browser browser;
  BrowserView browserView;
  CookieStore store;
private String url;
 
  public JXBrowser(JXBFactory jxb) {
    this.jxb = jxb;
    browser = jxb.engine.newBrowser();
    store   = jxb.engine.cookieStore();
    browser.settings().enableTransparentBackground();
    browser.on(TitleChanged.class, this::onTitle);
    browser.on(StatusChanged.class, this::onStatus);
    browser.on(ConsoleMessageReceived.class, this::onMessage);
	browser.navigation().on(NavigationStarted.class, this::navigationStarted );
	browser.navigation().on(NavigationRedirected.class, this::navigationRedirected);
    stub = new API();
    stub.terminator = this;
    browser.set(InjectJsCallback.class, params -> {
      installAPI(params.frame());
      return InjectJsCallback.Response.proceed();
    });
  }

  void navigationStarted(NavigationStarted event) {
	  if (console != null) {
		  ConsoleEvent ev = new ConsoleEvent(this, Level.DEBUG, "NavigationStarted:" + event.url());
		  console.onConsole(ev);
	  }
  }

  void navigationRedirected(NavigationRedirected event) {
	  if (console != null) {
		  ConsoleEvent ev = new ConsoleEvent(this, Level.DEBUG, "NavigationRedirected:" + event.destinationUrl());
		  console.onConsole(ev);
	  }
  }
  
  
  void onTitle(TitleChanged event) {
    TitleListener l = title;
    if (l != null) {
      l.onTitle(new TitleEvent(this, event.title()));
    }
  }
  
  void onStatus(StatusChanged event) {
    StatusListener l = status;
    if (l != null) {
      l.onStatus(new StatusEvent(this, event.statusText()));
    }
  }
  
  void onMessage(ConsoleMessageReceived event) {
    ConsoleListener l = console;
    if (l != null) {
      ConsoleEvent ev = new ConsoleEvent(this, toLevel(event.consoleMessage().level()), event.consoleMessage().message());
      l.onConsole(ev);
    }
  }
  
  private static Level toLevel(ConsoleMessageLevel level) {
    switch(level) {
      case LOG:  
    	return Level.INFO;
      case VERBOSE:
      case DEBUG:
        return Level.DEBUG;
      case LEVEL_ERROR:
        return Level.ERROR;
      case WARNING:
        return Level.WARN;
      case UNRECOGNIZED:
      case CONSOLE_MESSAGE_LEVEL_UNSPECIFIED:
    }
    return Level.LOG;
  }

  public void installAPI(Frame frame) {
    if (!frame.isMain()) return;
    JsObject window = frame.executeJavaScript("window");
    window.putProperty("API", stub);
    window.putProperty("API_1484_11", stub);
  }

  @Override
  public void close() throws IOException {
    browser.set(InjectJsCallback.class, params -> InjectJsCallback.Response.proceed());
    browser.close();
  }
  
  @Override
  protected void finalize() throws Throwable {
    close();
  }

  @Override
  public JComponent asComponent() {
    if (browserView == null) 
      browserView = BrowserView.newInstance(browser);
    return browserView;
  }

  @Override
  public void loadContent(String content, String type) {
    MimeType mimeType = MimeType.of(type);
    LoadDataParams data = LoadDataParams.newBuilder(content).mimeType(mimeType).build();
    browser.mainFrame().get().loadData(data );
  }

  @Override
  public void loadURL(String url) {
    if (url == null) url = "about:blank";
    this.url = url;
    browser.navigation().loadUrl(url);
  }

  @Override
  public void setAPI(SCORM2004APIInterface api) {
    stub.delegate = api;
  }

  @Override
  public void addTitleListener(TitleListener l) {
    title = l;
  }

  @Override
  public void removeTitleListener(TitleListener l) {
    title = null;
  }

  @Override
  public void addRefreshListener(RefreshListener l) {
    refresh = l;
  }

  @Override
  public void removeRefreshListener(RefreshListener l) {
    refresh = null;
  }

  @Override
  public void addStatusListener(StatusListener l) {
    status = l;
  }

  @Override
  public void removeStatusListener(StatusListener l) {
    status = null;

  }

  @Override
  public void addConsoleListener(ConsoleListener l) {
    console = l;

  }

  @Override
  public void removeConsoleListener(ConsoleListener l) {
    console = this;

  }

  boolean isClosed() {
    return browser.isClosed();
  }

	@Override
	public void run() {
		List<Cookie> list = store.cookies(url);
		for (Cookie item: list) {
			if (item.name().startsWith("dwo"))
				stub.SetValue(item.name(), item.value());
		}
	}

	@Override
	public void onConsole(ConsoleEvent event) {
	}
}
