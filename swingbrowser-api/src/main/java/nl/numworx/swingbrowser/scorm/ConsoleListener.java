package nl.numworx.swingbrowser.scorm;

import java.util.EventListener;

import nl.numworx.swingbrowser.api.ConsoleEvent;

public interface ConsoleListener extends EventListener {

  void onConsole(ConsoleEvent event);
}
