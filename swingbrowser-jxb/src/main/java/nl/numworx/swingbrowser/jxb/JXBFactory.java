package nl.numworx.swingbrowser.jxb;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.EngineOptions.Builder;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.permission.callback.RequestPermissionCallback;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JXBFactory implements SwingBrowserFactory {

  static boolean isMac = System.getProperty("os.name").contains("Mac OS X");
	
	
  public JXBFactory() {


    String licence;
    licence = System.getProperty("jxbrowser.license.key", JXB.licence);
    String remoteDebuggingPort = System.getProperty("jxbrowser.remote.debugging.port", "-1");
    
    Builder builder = EngineOptions.newBuilder(RenderingMode.OFF_SCREEN);
    try {
    	int port = Integer.parseInt(remoteDebuggingPort);
    	if (port > 0)
    		builder = builder.remoteDebuggingPort(port);
    } catch(Exception nop) {}

    if (isMac) {
    	builder = builder
    	        .addSwitch("--disable-features=NativeNotifications")
    	;
    }
    
    EngineOptions options = builder
        .licenseKey(licence)
        .language(Language.of(JComponent.getDefaultLocale()).orElse(Language.ENGLISH_US)) // Language.of(Locale)
        .build();
    engine = Engine.newInstance(options);
	engine.permissions().set(RequestPermissionCallback.class, (params, tell) -> {
		tell.grant();
	});
  }
  
  Engine engine;
  
  @Override
  public SwingBrowser newBrowser() {
    return new JXBrowser(this);
  }

  public void close() {
    engine.close();
  }

  @Override
  protected void finalize() throws Throwable {
    close();
  }

  @Override
  public void newSession() {
    engine.cookieStore().deleteAll();
  }

}
