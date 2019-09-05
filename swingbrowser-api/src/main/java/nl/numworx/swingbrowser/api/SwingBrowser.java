/**
 * 
 */
package nl.numworx.swingbrowser.api;

import java.io.Closeable;

import javax.swing.JComponent;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

/**
 * @author wim
 *
 */
public interface SwingBrowser extends Closeable {
  JComponent asComponent();
  void loadContent(String content, String type);
  void loadURL(String url);
  void setAPI(SCORM2004APIInterface api);
}
