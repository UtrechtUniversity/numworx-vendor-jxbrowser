package nl.numworx.swingbrowser.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;

@SuppressWarnings("serial")
public class LocalServlet extends HttpServlet {
	
	final Logger LOG = Logger.getLogger(getClass().getName());

	public void log(String message) {
		LOG.info(message);
	}
	
	
	@Override
	public void log(String message, Throwable t) {
		LOG.log(Level.INFO, message, t);
	}


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
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter w = resp.getWriter();
			w.print(message);
			return;
		}
		if ("/local/scorm.js".equals(path)) {
			resp.setContentType("text/javascript");
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
		
		if (path.startsWith("/local/resources")) {
			
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
            String origin = req.getHeader("Origin");
            if (origin == null) {
                origin = "*";
            }
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Access-Control-Expose-Headers", "content-type");
            resp.setHeader("Access-Control-Allow-Credentials", "true");

            resp.sendError(HttpServletResponse.SC_NO_CONTENT);
			
			if ("/Commit".equals(tail))
				api.Commit("");
			else if ("/Terminate".equals(tail))
				api.Terminate("");
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = "*";
        }
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
        response.setHeader("Access-Control-Expose-Headers", "content-type");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, authorization, x-http-method-override");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.getOutputStream().close();
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		super.service(req, res);
	}

}
