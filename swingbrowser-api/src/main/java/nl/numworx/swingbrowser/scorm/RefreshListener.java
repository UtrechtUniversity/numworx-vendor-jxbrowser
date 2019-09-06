package nl.numworx.swingbrowser.scorm;

import java.util.EventListener;

import nl.numworx.swingbrowser.api.RefreshEvent;

public interface RefreshListener extends EventListener {
  void onRefresh(RefreshEvent event);
}
