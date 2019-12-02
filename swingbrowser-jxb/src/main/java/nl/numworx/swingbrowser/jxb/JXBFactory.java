package nl.numworx.swingbrowser.jxb;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.Language;
import com.teamdev.jxbrowser.engine.RenderingMode;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;

public class JXBFactory implements SwingBrowserFactory {

  public JXBFactory() {

    //System.setProperty("jxbrowser.license.key", "1BNDHFSC1FSXE8ZQ3CGF91WIOWKO39P18F6JHR1D9G9NYT5IPV0ZEJAIRCA8F5K1040G6S");

    String licence = "1BNDHFSC1FSXE8ZQ3CGF91WIOWKO39P18F6JHR1D9G9NYT5IPV0ZEJAIRCA8F5K1040G6S";
    licence = "1BNDIEOFAYVK0JG2LUSYWT2QRQ11IJ40O41BARIKQGLTO7WV9Z7P4UANHYCT6NPO44EPC4";
    
    licence = System.getProperty("jxbrowser.license.key", licence);
    EngineOptions options = EngineOptions.newBuilder(RenderingMode.OFF_SCREEN)
        .licenseKey(licence)
        .language(Language.of(JComponent.getDefaultLocale()).orElse(Language.ENGLISH_US)) // Language.of(Locale)
        .build();
    engine = Engine.newInstance(options);
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
