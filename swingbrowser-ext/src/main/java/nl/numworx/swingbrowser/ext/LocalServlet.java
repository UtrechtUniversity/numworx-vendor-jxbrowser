package nl.numworx.swingbrowser.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

public class LocalServlet extends HttpServlet {

	private String message = "";
	private SCORM2004APIInterface api;	
	
	/**
	 * @param api2
	 */
	LocalServlet(SCORM2004APIInterface api2) {
		this.api = api2;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURI();
		log("doGet " + path);
		if ("/local/".equals(path)) {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter w = resp.getWriter();
			w.print(message);
			return;
		}
		if ("/local/scorm.js".equals(path)) {
			resp.setContentType("text/javascript");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter w = resp.getWriter();
			
			w.println("/* SCORM */");
			w.println("scorm = ");
			JSONObject scorm = new JSONObject();
			scorm.put("dme.abo_type", "premium");
			if (api != null) {
				String ld = api.GetValue("cmi.launch_data");
				if (ld != null && ! ld.isEmpty()) {
					scorm.put("cmi.launch_data", ld);
				}
			}
			scorm.writeJSONString(w);
			return;
		}
		
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public void init() throws ServletException {
		String url = getInitParameter("url");
		String local = getInitParameter("local");
		try {
			InputStream in = getClass().getResourceAsStream("resources/index.tmpl");
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[4096];
			int len;
			while ( (len = reader.read(buffer)) >= 0) {
				builder.append(buffer, 0, len);
			}
			reader.close();
			message = java.text.MessageFormat.format(builder.toString(), url, local);
		} catch (IOException e) {
			log("init", e);
			throw new ServletException(e.getMessage(), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			BufferedReader in = req.getReader();
			String tail = req.getPathInfo();
			try {
				Map<String,String> object = (Map<String, String>) new JSONParser().parse(in);
				object.forEach(api::SetValue);
			} catch (ParseException e) {
				log("Parser", e);
			}				
			resp.sendError(HttpServletResponse.SC_NO_CONTENT);
			
			if ("/Commit".equals(tail))
				api.Commit("");
			else if ("/Terminate".equals(tail))
				api.Terminate("");
	}

}
