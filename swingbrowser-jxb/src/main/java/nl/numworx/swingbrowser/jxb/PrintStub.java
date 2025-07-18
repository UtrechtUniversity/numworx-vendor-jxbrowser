package nl.numworx.swingbrowser.jxb;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.PrintCallback;
import com.teamdev.jxbrowser.browser.callback.PrintHtmlCallback;
import com.teamdev.jxbrowser.print.PdfPrinter;
import com.teamdev.jxbrowser.print.PrintJob;
import com.teamdev.jxbrowser.print.event.PrintCompleted;

import nl.numworx.swingbrowser.print.PrintEvent;
import nl.numworx.swingbrowser.print.PrintListener;
import nl.numworx.swingbrowser.print.Printing;

class PrintStub implements Printing {

	private JXBrowser jxb;

	public PrintStub(JXBrowser jxBrowser) {
		this.jxb = jxBrowser;
	}

	private PrintListener pl;
	
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
		    Path path = Paths.get(new File("printing.pdf").toURI());
			printJob.settings().pdfFilePath(path).apply();
		    printJob.on(PrintCompleted.class, event -> {
		        if (event.isSuccess()) {
		            jxb.info("Printing is completed successfully.");
		        } else {
		            jxb.warning("Printing has failed.");
		        }
		        if (pl != null)
		        	pl.onPrint(new PrintEvent(this));
		    });
		    tell.proceed(printer);
		});
//		browser.navigation().on(LoadFinished.class, ev -> 	
			browser.mainFrame().get().print()
//		)
;		
	}

}
