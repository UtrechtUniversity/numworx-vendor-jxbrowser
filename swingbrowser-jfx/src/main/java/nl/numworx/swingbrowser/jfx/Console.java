package nl.numworx.swingbrowser.jfx;


import nl.numworx.swingbrowser.api.ConsoleEvent;
import nl.numworx.swingbrowser.scorm.ConsoleListener;

public class Console {
  
    ConsoleListener delegate;
    Object source;
    
    Console(Object source) {
      this.source = source;
    }

    private void log(ConsoleEvent.Level level, Object message) {
      if (delegate != null) {
        delegate.onConsole(new ConsoleEvent(source, level, String.valueOf(message)));
      }
    }
    	
	public void log(Object o) {
		log(ConsoleEvent.Level.LOG, String.valueOf(o));
	}
	
	public void log(Object first, Object... message) {
		StringBuilder sb = new StringBuilder();
		sb.append(first);
		for(Object item : message) {
			sb.append(item);
		}
		log(sb);
	}
	
	public void error(Object msg) {
		log(ConsoleEvent.Level.ERROR, msg);
	}

	public void warn(Object msg) {
		log(ConsoleEvent.Level.WARN, msg);
	}
	public void info(Object msg) {
		log(ConsoleEvent.Level.INFO, msg);
	}
	public void debug(Object msg) {
		log(ConsoleEvent.Level.DEBUG, msg);
	}
}
