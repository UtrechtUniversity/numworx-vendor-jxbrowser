package nl.numworx.swingbrowser.print;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PrintEvent extends EventObject {
	
	private final boolean success;

	public PrintEvent(Object source) {
		this(source, true);
	}

	public PrintEvent(Object source, boolean b) {
		super(source);
		success = b;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

}
