package nl.numworx.swingbrowser.jxb;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.callback.Callback;
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

    //System.setProperty("jxbrowser.license.key", "1BNDHFSC1FSXE8ZQ3CGF91WIOWKO39P18F6JHR1D9G9NYT5IPV0ZEJAIRCA8F5K1040G6S");

    String licence;
//    licence = "1BNDHFSC1FSXE8ZQ3CGF91WIOWKO39P18F6JHR1D9G9NYT5IPV0ZEJAIRCA8F5K1040G6S";
//    licence = "1BNDIEOFAYVK0JG2LUSYWT2QRQ11IJ40O41BARIKQGLTO7WV9Z7P4UANHYCT6NPO44EPC4";
//    licence = "1BNDIEOFAYZ5UWORS4IPT18EK6ZN4JGH5XBV8TG6QREOYC1WU89A3L95AUCT6LQLRVA0K9";
//    licence = "1BNDIEOFAZ2RP9XGYE8GP9E0UHUDN9413J2UX7SR3Z6O63U2WKVGTEBBSMKU10PUBWM82T";
    licence = "1BNDIEOFAZ6DJN664NY7LHJTVF4078K5DJSRRCC0EI3307A92ZT53XBL6PK0NA2YDCMDRN";
    licence = System.getProperty("jxbrowser.license.key", licence);
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
