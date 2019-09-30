package nl.numworx.swingbrowser.jxb;

import java.awt.Container;
import java.io.IOException;

import javax.swing.JComponent;

import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.frame.LoadDataParams;
import com.teamdev.jxbrowser.net.MimeType;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

public class PooledJXBFactory extends JXBFactory implements SwingBrowserFactory {

  ObjectPool<SwingBrowser> pool;
  
  class SwingBrowserWrap implements SwingBrowser {
    final JXBrowser delegate;

    SwingBrowserWrap(JXBrowser delegate) {
      this.delegate = delegate;
    }

    public int hashCode() {
      return delegate.hashCode();
    }

    public void installAPI(Frame frame) {
      delegate.installAPI(frame);
    }

    public void close() throws IOException {
      if (!delegate.isClosed())
      { 
        setAPI(null);
        removeConsoleListener(null);
        removeRefreshListener(null);
        removeStatusListener(null);
        removeTitleListener(null);
        delegate.browser.navigation().loadUrlAndWait("about:blank");
        Container p = asComponent().getParent(); if (p != null) p.remove(asComponent()); else asComponent().invalidate();
        delegate.browserView = null;
        pool.returnObject(this);
      }
    }

    public JComponent asComponent() {
      return delegate.asComponent();
    }

    public void loadContent(String content, String type) {
      delegate.loadContent(content, type);
    }

    public void loadURL(String url) {
      delegate.loadURL(url);
    }

    public void setAPI(SCORM2004APIInterface api) {
      delegate.setAPI(api);
    }

    public void addTitleListener(TitleListener l) {
      delegate.addTitleListener(l);
    }

    public void removeTitleListener(TitleListener l) {
      delegate.removeTitleListener(l);
    }

    public void addRefreshListener(RefreshListener l) {
      delegate.addRefreshListener(l);
    }

    public void removeRefreshListener(RefreshListener l) {
      delegate.removeRefreshListener(l);
    }

    public void addStatusListener(StatusListener l) {
      delegate.addStatusListener(l);
    }

    public void removeStatusListener(StatusListener l) {
      delegate.removeStatusListener(l);
    }

    public void addConsoleListener(ConsoleListener l) {
      delegate.addConsoleListener(l);
    }

    public void removeConsoleListener(ConsoleListener l) {
      delegate.removeConsoleListener(l);
    }
    
  }
  
  PooledJXBFactory() {
    pool = new ObjectPool<SwingBrowser>(0) {
      
      @Override
      protected SwingBrowser createObject() {
        return new SwingBrowserWrap(new JXBrowser(PooledJXBFactory.this));
      }
    };
  }


  @Override
  public SwingBrowser newBrowser() {
    return pool.borrowObject();
  }


  @Override
  public void close() {
    pool.shutdown();
    super.close();
  }

}
