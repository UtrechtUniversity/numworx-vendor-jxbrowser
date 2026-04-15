package nl.numworx.swingbrowser.ext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
	List<String> allcookies = new ArrayList<>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rel = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null)
			rel += "?" + query;
		URL url = new URL(base.toURL(), rel);		
		URLConnection connection = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) connection;
		insertCookies(http);
		doTail(resp, http);	
	}

	private void doTail(HttpServletResponse resp, HttpURLConnection http) throws IOException {
		
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
			String type = http.getContentType();
			if(type != null) resp.setContentType(type);
		}
		byte[] buffer = new byte[4096];
		InputStream in = http.getInputStream();
		ServletOutputStream out = resp.getOutputStream();
		int len;
		while ( (len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		Map<String, List<String>> headers = http.getHeaderFields();
		// find cookies in headers.
		System.err.println(headers);
		
		List<String> cookies = headers.get("Set-Cookie");
		if (cookies != null) allcookies.addAll(cookies);
		
	}

	private void insertCookies(URLConnection http) {
		for (String value: allcookies) {
			int index;
			index = value.indexOf(';');
			if (index > 0) value = value.substring(0, index);
			http.setRequestProperty("Cookie", value);
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

	
	/**
	 * for auth2client dwo-redirect.
	 * expect 302 if password correct, or 200 for a retry 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rel = req.getRequestURI();
		String query = req.getQueryString();
		if (query != null)
			rel += "?" + query;
		URL url = new URL(base.toURL(), rel);		
		URLConnection connection = url.openConnection();
		insertCookies(connection);
		HttpURLConnection http = (HttpURLConnection) connection;
		http.setRequestMethod("POST"); // PUT is another valid option
		http.setDoOutput(true);
		// Source - https://stackoverflow.com/a/35013372
		// Posted by Ferrybig, modified by community. See post 'Timeline' for change history
		// Retrieved 2026-04-15, License - CC BY-SA 3.0

		Map<String,String[]> arguments  = new HashMap<>(); // unsupported getParameterMap();
		Enumeration<String> keys = req.getParameterNames();
		while (keys.hasMoreElements()) {
			String key =  keys.nextElement();
			arguments.put(key, req.getParameterValues(key));
		}
		
		StringJoiner sj = new StringJoiner("&");
		for(Map.Entry<String,String[]> entry : arguments.entrySet())
		    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
		         + URLEncoder.encode(entry.getValue()[0], "UTF-8"));
		byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
		int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		http.connect();
		try(OutputStream os = http.getOutputStream()) {
		    os.write(out);
		}
		// Do something with http.getInputStream()
		doTail(resp,http);
	}

	public void newSession() {
		allcookies.clear();
	}

}
