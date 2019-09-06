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
    EngineOptions options = EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
        .licenseKey("1BNDHFSC1FSXE8ZQ3CGF91WIOWKO39P18F6JHR1D9G9NYT5IPV0ZEJAIRCA8F5K1040G6S")
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

}
