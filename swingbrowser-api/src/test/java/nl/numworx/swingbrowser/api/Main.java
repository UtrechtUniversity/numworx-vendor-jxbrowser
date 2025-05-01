package nl.numworx.swingbrowser.api;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import nl.numworx.swingbrowser.print.PrintEvent;
import nl.numworx.swingbrowser.print.PrintListener;
import nl.numworx.swingbrowser.print.Printing;

public class Main {

  static Logger LOG = Logger.getLogger(Main.class.getName());
  static class PrintAction extends AbstractAction implements PrintListener {
	  Optional<Printing> printing;

	@Override
	public void actionPerformed(ActionEvent e) {
		printing.get().start();
	}

	public PrintAction(String name, Optional<Printing> printing) {
		super(name);
		this.printing = printing;
		setEnabled(printing.isPresent());
		printing.ifPresent(c -> c.addPrintListener(this));
	}

	@Override
	public void onPrint(PrintEvent event) {
		LOG.info("on printing " + event);
	}
	
  }
	
	
  public static void main(String[] args) {
    SwingBrowserProvider provider = new SwingBrowserProvider();
    
    SwingBrowserFactory factory = provider.getFactory();
    
    SwingBrowser browser = factory.newBrowser();
    
    JFrame frame = new JFrame("Swingbrowser");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(browser.asComponent());
    JMenuBar mb = new JMenuBar();
    JMenu file = new JMenu("File");
    file.add(new PrintAction("Print...", browser.printing()));
    mb.add(file);
    
	frame.setJMenuBar(mb);
    
    browser.loadURL("http://www.numworx.nl/");
    
    frame.setSize(1024, 768);
    frame.pack();
    frame.show();

  }

}
