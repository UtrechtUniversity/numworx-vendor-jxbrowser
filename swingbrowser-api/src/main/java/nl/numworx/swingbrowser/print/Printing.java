package nl.numworx.swingbrowser.print;

public interface Printing {
	void addPrintListener(PrintListener listener);
	void removePrintListener(PrintListener listener);
	
	void start();
}
