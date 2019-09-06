package nl.numworx.swingbrowser.scorm;

import java.util.EventListener;

import nl.numworx.swingbrowser.api.StatusEvent;

public interface StatusListener extends EventListener {
  void onStatus(StatusEvent event);
}
