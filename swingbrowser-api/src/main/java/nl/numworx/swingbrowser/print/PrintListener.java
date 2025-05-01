package nl.numworx.swingbrowser.print;

import java.util.EventListener;

public interface PrintListener extends EventListener {
	void onPrint(PrintEvent event);
}
