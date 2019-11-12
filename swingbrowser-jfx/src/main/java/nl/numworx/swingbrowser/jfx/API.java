package nl.numworx.swingbrowser.jfx;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

public final class API {
  SCORM2004APIInterface delegate;
  
  public String Initialize(String arg) {
    if(delegate != null) 
      return delegate.Initialize(arg);
    return "true";
  }
  public String LMSInitialize(String arg) {
    return Initialize(arg);
  }

  public String toString() {
    return super.toString();
  }
  
  public String GetValue(String arg) {
    if (delegate != null) 
      return delegate.GetValue(arg);
    return "";
  }

  public String SetValue(String key, String value) {
    if (delegate != null) 
      return delegate.SetValue(key, value);
    return "true";
  }

  public String GetLastError() {
    if (delegate != null)
      return delegate.GetLastError();
    return "0";
  }

  public String Commit(String arg) {
    if (delegate != null)
      return delegate.Commit(arg);
    return "true";
  }
 
  public String Terminate(String arg) {
    if (delegate != null)
      return delegate.Terminate(arg);
    return "true";
  }
  
  public String LMSFinish(String arg) {
    return Terminate(arg);
  }
  
  public String LMSSetValue(String key, String value) {
    return SetValue(key, value);
  }
  
}

