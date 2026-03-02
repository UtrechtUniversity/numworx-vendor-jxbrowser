package nl.numworx.swingbrowser.print;

import java.awt.print.PageFormat;
import java.io.File;

public interface Printing {
	void addPrintListener(PrintListener listener);
	void removePrintListener(PrintListener listener);
	void setPDFOutput(File output);
	void setPageFormat(PageFormat format);
	void start();
}
