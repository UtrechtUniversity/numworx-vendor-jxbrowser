package nl.numworx.swingbrowser.api;

import java.util.EventObject;

public class StatusEvent extends EventObject {

  private final String status;

  public StatusEvent(Object source, String status) {
    super(source);
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
