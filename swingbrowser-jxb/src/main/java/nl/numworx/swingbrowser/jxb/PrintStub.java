package nl.numworx.swingbrowser.jxb;

import java.awt.print.PageFormat;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.PrintCallback;
import com.teamdev.jxbrowser.browser.callback.PrintHtmlCallback;
import com.teamdev.jxbrowser.print.Orientation;
import com.teamdev.jxbrowser.print.PageMargins;
import com.teamdev.jxbrowser.print.PaperSize;
import com.teamdev.jxbrowser.print.PdfPrinter;
import com.teamdev.jxbrowser.print.PdfPrinter.HtmlSettings;
import com.teamdev.jxbrowser.print.PrintJob;
import com.teamdev.jxbrowser.print.event.PrintCompleted;

import nl.numworx.swingbrowser.print.PrintEvent;
import nl.numworx.swingbrowser.print.PrintListener;
import nl.numworx.swingbrowser.print.Printing;

class PrintStub implements Printing {

	private JXBrowser jxb;

	public PrintStub(JXBrowser jxBrowser) {
		this.jxb = jxBrowser;
		
		format = new PageFormat(); // default A4 portrait....
	}

	private PrintListener pl;
	private File output;
	private PageFormat format;
	
	@Override
	public void addPrintListener(PrintListener listener) {
		pl = listener;
	}

	@Override
	public void removePrintListener(PrintListener listener) {
		if( pl == listener) pl = null;
	}

	@Override
	public void start() {
		final Browser browser = jxb.browser;
		browser.set(PrintCallback.class, 
				(params, tell) -> tell.print()
		);
		browser.set(PrintHtmlCallback.class, (params, tell) -> {
		    PdfPrinter<com.teamdev.jxbrowser.print.PdfPrinter.HtmlSettings> printer = params.printers().pdfPrinter();
		    PrintJob<com.teamdev.jxbrowser.print.PdfPrinter.HtmlSettings> printJob = printer.printJob();
		    Path path = Paths.get(output.toURI());
			HtmlSettings settings = printJob.settings();
			settings.pdfFilePath(path)
			 .enablePrintingBackgrounds()
			 .disablePrintingHeaderFooter()
			 .orientation(Orientation.LANDSCAPE)
			 .paperSize(PaperSize.ISO_A4)
			 .pageMargins(PageMargins.of(18, 18, 18, 18))
			 .apply();
		    printJob.on(PrintCompleted.class, event -> {
		        if (event.isSuccess()) {
		            jxb.info("Printing is completed successfully.");
		        } else {
		            jxb.warning("Printing has failed.");
		        }
		        if (pl != null)
		        	pl.onPrint(new PrintEvent(this, event.isSuccess()));
		    });
		    tell.proceed(printer);
		});
		browser.mainFrame().get().print();		
	}

	@Override
	public void setPDFOutput(File output) {
		this.output = output;
	}

	@Override
	public void setPageFormat(PageFormat format) {
		this.format = format;
	}

}
