package nl.numworx.swingbrowser.jxb;

import javax.swing.JFrame;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.api.SwingBrowserProvider;

public class Main {

  public static void main(String[] args) {
    SwingBrowserProvider provider = new SwingBrowserProvider();
    
    SwingBrowserFactory factory = provider.getFactory();
    
    SwingBrowser browser = factory.newBrowser();
    
    JFrame frame = new JFrame("Swingbrowser");
    frame.setContentPane(browser.asComponent());
    browser.addTitleListener(t -> frame.setTitle(t.getTitle()));
    browser.loadURL("http://www.numworx.nl/");
    
    frame.setSize(1024, 768);
    frame.pack();
    frame.show();
  }
}
