/**
 * 
 */
package nl.numworx.swingbrowser.api;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.Optional;

import javax.swing.JComponent;

import nl.numworx.swingbrowser.scorm.ConsoleListener;
import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.numworx.swingbrowser.scorm.StatusListener;
import nl.numworx.swingbrowser.scorm.TitleListener;

/**
 * @author wim
 *
 */
public interface SwingBrowser extends Closeable {
  JComponent asComponent();
  void loadContent(String content, String type);
  void loadURL(String url);
  void setAPI(SCORM2004APIInterface api);
  
  void addTitleListener(TitleListener l);
  void removeTitleListener(TitleListener l);
  
  void addRefreshListener(RefreshListener l);
  void removeRefreshListener(RefreshListener l);
  
  void addStatusListener(StatusListener l);
  void removeStatusListener(StatusListener l);
  
  void addConsoleListener(ConsoleListener l);
  void removeConsoleListener(ConsoleListener l);
  
  default void setSize(int width, int height) { }
  default void loadContentAndWait(String content, String type) { loadContent(content, type); }
  default Optional<BufferedImage> bitmap() { return Optional.empty(); }
}
