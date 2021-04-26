package nl.numworx.swingbrowser.ext;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PreviewServlet extends HttpServlet {

	@Override
	public String getServletName() {
		return getClass().getName();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	URI base;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rel = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null)
			rel += "?" + query;
		URL url = new URL(base.toURL(), rel);		
		URLConnection connection = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) connection;
		http.setInstanceFollowRedirects(false);

		int code = HttpServletResponse.SC_NOT_FOUND;
		try { code = http.getResponseCode(); } 
		catch(Exception oops) { 
			log("oops", oops);
		}

		if (code != 200) {
			if (code == 302) {
				resp.sendRedirect(http.getHeaderField("Location"));
				return;
			} else
			{
				resp.sendError(code, http.getResponseMessage());
			}
		
			Map<String, List<String>> m = http.getHeaderFields();
			m.forEach( (k,v) -> {
				if (k != null)
					v.forEach(vv -> resp.setHeader(k, vv));
			});
			return;
		} else {
			String type = connection.getContentType();
			resp.setContentType(type);
		}
		byte[] buffer = new byte[4096];
		InputStream in = connection.getInputStream();
		ServletOutputStream out = resp.getOutputStream();
		int len;
		while ( (len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
	
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	public void init() throws ServletException {
		//log("inited");
		try {
			base = new URI(getInitParameter("url"));
		} catch (URISyntaxException e) {
			log("init url " + getInitParameter("url"), e);
			throw new ServletException("init",e);
		}
	}

	@Override
	public void destroy() {
		log("destroyed");
	}



}
