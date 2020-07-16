package nl.numworx.swingbrowser.jxb;

import com.teamdev.jxbrowser.js.JsAccessible;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

public final class API {
  SCORM2004APIInterface delegate;
  Runnable terminator = () -> {};
  
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
    if (delegate != null) 
      return delegate.GetValue(arg);
    return "";
  }
  @JsAccessible
  public String SetValue(String key, String value) {
    if (delegate != null) 
      return delegate.SetValue(key, value);
    return "true";
  }
  
  @JsAccessible
  public String LMSSetValue(String key, String value) {
    return SetValue(key, value);
  }
  

  @JsAccessible
  public String GetLastError() {
    if (delegate != null)
      return delegate.GetLastError();
    return "0";
  }
  @JsAccessible
  public String Commit(String arg) {
    if (delegate != null)
      return delegate.Commit(arg);
    return "true";
  }
 
  @JsAccessible
  public String Terminate(String arg) {
	terminator.run();
    if (delegate != null)
      return delegate.Terminate(arg);
    return "true";
  }
  
  @JsAccessible
  public String LMSFinish(String arg) {
    return Terminate(arg);
  }
}