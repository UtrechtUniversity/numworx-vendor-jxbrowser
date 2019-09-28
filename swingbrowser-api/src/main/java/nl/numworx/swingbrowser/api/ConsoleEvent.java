package nl.numworx.swingbrowser.api;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ConsoleEvent extends EventObject {

  public enum Level { LOG, ERROR, WARN, INFO, DEBUG };
  
  private final Level level;
  private final String message;
  
  public Level getLevel() {
    return level;
  }

  public String getMessage() {
    return message;
  }

  public ConsoleEvent(Object source, Level level, String message) {
    super(source);
    this.level = level;
    this.message = message;
  }

}
