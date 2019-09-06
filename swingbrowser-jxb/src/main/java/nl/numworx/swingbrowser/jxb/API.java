package nl.numworx.swingbrowser.jxb;

import com.teamdev.jxbrowser.js.JsAccessible;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

public final class API {
  SCORM2004APIInterface delegate;
  
  @JsAccessible
  public String Initialize(String arg) {
    if(delegate != null) 
      return delegate.Initialize(arg);
    return "true";
  }
  @JsAccessible
  public String LMSInitialize(String arg) {
    return Initialize(arg);
  }

  @JsAccessible 
  public String toString() {
    return super.toString();
  }
  
  @JsAccessible
  public String GetValue(String arg) {
    System.out.println("get value of " + arg);
    if (delegate != null) 
      return delegate.GetValue(arg);
    return "";
  }
  @JsAccessible
  public String SetValue(String key, String value) {
    System.out.println("set value for " + key);
    if (delegate != null) 
      return delegate.SetValue(key, value);
    return "true";
  }

  @JsAccessible
  public String GetLastError() {
    if (delegate != null)
      return delegate.GetLastError();
    return "0";
  }
  @JsAccessible
  public String Commit(String arg) {
    System.out.println("Commit");
    if (delegate != null)
      return delegate.Commit(arg);
    return "true";
  }
 
  @JsAccessible
  public String Terminate(String arg) {
    System.out.println("Terminate");
    if (delegate != null)
      return delegate.Terminate(arg);
    return "true";
  }
}