package nl.numworx.swingbrowser.jxb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.teamdev.jxbrowser.ui.Bitmap;
import com.teamdev.jxbrowser.view.swing.graphics.BitmapImage;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.api.SwingBrowserProvider;

public class ImageMagic {


	public static void main(String[] args) throws Exception {
	    
	    JXBFactory factory = new JXBFactory();
	    
	    SwingBrowser browser = factory.newBrowser();
	    InputStream in = ImageMagic.class.getResourceAsStream("/easypeasy.svg");
	      byte[] data= new byte[in.available()];
	      in.read(data);
	      in.close();
	      String content = new String(data, "UTF-8");
	      browser.setSize(300,300);
	      browser.loadContentAndWait(content, "image/svg+xml");
	      BufferedImage image = browser.bitmap().get();
	      JFrame f = new JFrame() { 
	    	  public void paint(Graphics g) {
//	    		  g.setColor(Color.GRAY);
//	    		  g.fillRect(0, 0, getWidth(), getHeight());
	    		  g.drawImage(image, 0, 0, null);
	    	  }
	      };
	      
	      f.setSize(image.getWidth(), image.getHeight());
	      f.setVisible(true);
	      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
