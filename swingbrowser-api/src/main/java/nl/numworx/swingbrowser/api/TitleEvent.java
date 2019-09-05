package nl.numworx.swingbrowser.api;

import java.util.EventObject;

public class TitleEvent extends EventObject {

  private final String title;
  
  public TitleEvent(Object source, String title) {
    super(source);
    this.title = title;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

}
