package nl.numworx.swingbrowser.jxb;

import java.awt.Container;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import com.teamdev.jxbrowser.frame.Frame;
import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

public class PooledJXBFactory extends JXBFactory implements SwingBrowserFactory {

  ObjectPool<SwingBrowserWrap> pool;
  
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
        new SwingWorker<SwingBrowserWrap,Void>() {

          @Override
          protected SwingBrowserWrap doInBackground() throws Exception {
            setAPI(null);
            removeConsoleListener(null);
            removeRefreshListener(null);
            removeStatusListener(null);
            removeTitleListener(null);
            delegate.browser.navigation().loadUrlAndWait("about:blank");
           return SwingBrowserWrap.this;
          }

          @Override
          protected void done() {
              try {
                get();
                if (delegate.browserView != null) {
                  Container p = asComponent().getParent(); 
                  if (p != null) p.remove(asComponent()); else asComponent().invalidate();
                  // eigenlijk een dispose nodig.
                  String version = com.teamdev.jxbrowser.VersionInfo.version();
                  if ("7.2".equals(version))
                  	delegate.browserView = null; 
                }
            } catch (Exception e) {
                try {
                  delegate.close();
                } catch (IOException e1) {
                }
                return;
              }
              pool.returnObject(SwingBrowserWrap.this);

          }
          
        }.execute();
        
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
    pool = new ObjectPool<SwingBrowserWrap>(1) {
      
      @Override
      protected SwingBrowserWrap createObject() {
        return new SwingBrowserWrap(new JXBrowser(PooledJXBFactory.this));
      }

      @Override
      protected void disposeObject(SwingBrowserWrap object) {
        try {
          object.delegate.close();
        } catch (IOException e) {
        }
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
