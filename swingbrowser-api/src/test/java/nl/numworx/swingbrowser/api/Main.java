package nl.numworx.swingbrowser.api;

import javax.swing.JFrame;

public class Main {

  public static void main(String[] args) {
    SwingBrowserProvider provider = new SwingBrowserProvider();
    
    SwingBrowserFactory factory = provider.getFactory();
    
    SwingBrowser browser = factory.newBrowser();
    
    JFrame frame = new JFrame("Swingbrowser");
    frame.setContentPane(browser.asComponent());
    
    browser.loadURL("http://www.numworx.nl/");
    
    frame.setSize(1024, 768);
    frame.pack();
    frame.show();

  }

}
