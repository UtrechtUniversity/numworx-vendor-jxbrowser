package nl.numworx.swingbrowser.scorm;

import java.util.EventListener;

import nl.numworx.swingbrowser.api.TitleEvent;

public interface TitleListener extends EventListener {
  void onTitle(TitleEvent event);
}
