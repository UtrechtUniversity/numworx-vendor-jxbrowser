package nl.numworx.swingbrowser.jfx;

import static javafx.concurrent.Worker.State.FAILED;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nl.numworx.swingbrowser.api.RefreshEvent;
import nl.numworx.swingbrowser.api.StatusEvent;
import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.TitleEvent;
import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEvent;

import javafx.event.EventHandler;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

@SuppressWarnings({"restriction", "serial"})
class JFXBrowser extends JFXPanel implements SwingBrowser {

  final class TitleHandler implements ChangeListener<String> {
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue,
            final String newValue) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    sendTitle(newValue);
                }
            });
        }

    void sendTitle(String newValue) {
      TitleListener l = title;
      if (l != null) {
        l.onTitle(new TitleEvent(JFXBrowser.this, newValue));
      }
      
    }
    }
  
  final class ExceptionHandler implements ChangeListener<Throwable> {
    public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
        if (engine.getLoadWorker().getState() == FAILED) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(JFXBrowser.this,
                            (value != null) ? engine.getLocation() + "\n" + value.getMessage()
                                    : engine.getLocation() + "\nUnexpected error.",
                            "Loading error...", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
  }
  
  final class StatusHandler implements EventHandler<WebEvent<String>> {
    @Override
    public void handle(final WebEvent<String> event) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          String msg = event.getData();
          StatusListener l = status;
          if (l != null)
            l.onStatus(new StatusEvent(JFXBrowser.this, msg));
        }
      });
    }
  }

  private WebEngine engine;
  private volatile TitleListener title;
  private volatile RefreshListener refresh;
  private volatile StatusListener status;
  private final Console stub = new Console(this);
  private final API api = new API();
  static {
    Platform.setImplicitExit(false);
  }

  JFXBrowser() {
    createScene();
  }

  public void repaint() {
    super.repaint();
    RefreshListener l = refresh;
    if (l != null) 
      l.onRefresh(new RefreshEvent(this));
    }

    void install() {
      JSObject window;
      window = (JSObject) engine.executeScript("window");
      window.setMember("console", stub);
      window.setMember("API", api);
      window.setMember("API_1484_11", api);
    }

    private void removeMembers0() {
      JSObject window;
      window = (JSObject) engine.executeScript("window");
      try { window.removeMember("console"); } catch (JSException e) {}
      try { window.removeMember("API"); } catch (JSException e) {}
      try { window.removeMember("API_1484_11"); } catch (JSException e) {}
    }


  private void createScene() {

    Platform.runLater(new Runnable() {

        @Override
        public void run() {

            WebView view = new WebView();
            engine = view.getEngine();
            install();

            engine.titleProperty().addListener(new TitleHandler());
            engine.setOnStatusChanged(new StatusHandler());

//          engine.locationProperty().addListener(new ChangeListener<String>() {
//              @Override
//              public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
//                  SwingUtilities.invokeLater(new Runnable() {
//                      @Override
//                      public void run() {
//                          txtURL.setText(newValue);
//                      }
//                  });
//              }
//          });

            engine.getLoadWorker().exceptionProperty().addListener(new ExceptionHandler());

            setScene(new Scene(view));
        }
    });
}

  
  
  @Override
  public void close() throws IOException {
    loadURL("about:blank");
    if ( Platform.isFxApplicationThread() )
      removeMembers0();
    else
      Platform.runLater(()-> { removeMembers0(); });
  }

  @Override
  public JComponent asComponent() {
    return this;
  }

  @Override
  public void loadContent(String content, String type) {
    Platform.runLater(() -> {
      engine.loadContent(content, type);
  });
  }

  @Override
  public void loadURL(String url) {
    Platform.runLater(() -> engine.load(url));
  }

  @Override
  public void setAPI(SCORM2004APIInterface api) {
    this.api.delegate = api;
  }

  @Override
  public void addTitleListener(TitleListener l) {
    title = l;
  }

  @Override
  public void removeTitleListener(TitleListener l) {
    title = null;
  }

  @Override
  public void addRefreshListener(RefreshListener l) {
    refresh = l;
  }

  @Override
  public void removeRefreshListener(RefreshListener l) {
    refresh = null;
  }

  @Override
  public void addStatusListener(StatusListener l) {
    status = l;
  }

  @Override
  public void removeStatusListener(StatusListener l) {
    status = null;
  }

  @Override
  public void addConsoleListener(ConsoleListener l) {
    stub.delegate = null;
  }

  @Override
  public void removeConsoleListener(ConsoleListener l) {
    stub.delegate = l;
  }

}
