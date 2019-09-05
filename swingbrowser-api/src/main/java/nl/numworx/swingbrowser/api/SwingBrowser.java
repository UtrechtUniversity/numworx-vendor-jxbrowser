/**
 * 
 */
package nl.numworx.swingbrowser.api;

import java.io.Closeable;

import javax.swing.JComponent;

import nl.numworx.swingbrowser.scorm.RefreshListener;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
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
}
