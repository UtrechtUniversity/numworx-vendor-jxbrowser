package nl.numworx.swingbrowser.jfx;

import static javafx.concurrent.Worker.State.FAILED;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nl.numworx.swingbrowser.api.RefreshEvent;
import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.TitleEvent;
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
 
  private WebEngine engine;
  private volatile TitleListener title;
  private volatile RefreshListener refresh;
  private volatile StatusListener status;

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

  private void createScene() {

    Platform.runLater(new Runnable() {

        @Override
        public void run() {

            WebView view = new WebView();
            engine = view.getEngine();

            engine.titleProperty().addListener(new TitleHandler());

//          engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
//              @Override
//              public void handle(final WebEvent<String> event) {
//                  SwingUtilities.invokeLater(new Runnable() {
//                      @Override
//                      public void run() {
//                          lblStatus.setText(event.getData());
//                      }
//                  });
//              }
//          });

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

//          engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
//              @Override
//              public void changed(ObservableValue<? extends Number> observableValue, Number oldValue,
//                      final Number newValue) {
//                  SwingUtilities.invokeLater(new Runnable() {
//                      @Override
//                      public void run() {
//                          progressBar.setValue(newValue.intValue());
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
    // TODO Auto-generated method stub

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

}
