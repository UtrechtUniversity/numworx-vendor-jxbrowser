package nl.numworx.swingbrowser.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
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
			String url = getInitParameter("url");
			String local = getInitParameter("local");
			String code = req.getParameter("code");
			String state = req.getParameter("state");
			local = append(local, code, state);
			String format = java.text.MessageFormat.format(message, url, local);
			w.print(format);
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

				ld = api.GetValue("dme.oauth._children");
				if (ld != null && ! ld.isEmpty()) {
					StringTokenizer st = new StringTokenizer(ld, " ,");
					while(st.hasMoreTokens()) {
						String key = "dme.oauth." + st.nextToken();
						scorm.put(key, api.GetValue(key));
					}
					int port = req.getServerPort();
					String host = req.getScheme() + "://" + req.getServerName() + ":" + port;
					URI u = URI.create(api.GetValue("dme.oauth.endpoint"));
					scorm.put("dme.oauth.endpoint", host + u.getRawPath());
					u = URI.create(api.GetValue("dme.oauth.redirect_uri"));
				    api.SetValue("dme.oauth.redirect_uri", host + u.getRawPath());
				}
				
			}
			scorm.writeJSONString(w);
			return;
		}
		
		if (path.startsWith("/local/resources")) {
			LOG.warning(" missing " + path);
		}
		String tail = req.getPathInfo();
		if ("/Terminate".equals(tail)) {
			String arg = req.getParameter("q");
			parseJSON(new StringReader(arg));
			//map.forEach((k,v)-> api.SetValue(k, v[0]);
// ons kent ons
			api.SetValue("dwoSAMLchallenge", api.GetValue("dme.oauth.code_challenge"));
			
			api.Terminate("");
			resp.getWriter().print("You may close this window.");
			return;
		}
		
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String append(String url, String code, String state) {
		char has = url.contains("?") ? '&' : '?';
		if (code != null) {
			url += has + "code=" + URLEncoder.encode(code);
			has = '&';
		}
		if (state != null) {
			url += has + "state=" + URLEncoder.encode(state);		
		}
		
		return url;
	}


	@Override
	public void init() throws ServletException {
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
			String url = getInitParameter("url");
			String local = getInitParameter("local");
			//message = java.text.MessageFormat.format(builder.toString(), url, local);
			message = builder.toString();
		} catch (IOException e) {
			log("init", e);
			throw new ServletException(e.getMessage(), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			BufferedReader in = req.getReader();
			String tail = req.getPathInfo();
			parseJSON(in);				
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


	private void parseJSON(Reader in) throws IOException {
		try {
			ContainerFactory factory = new ContainerFactory() {
				
				@Override
				public Map createObjectContainer() {
					return new LinkedHashMap();
				}
				
				@Override
				public List creatArrayContainer() {
					return new JSONArray();
				}
			};
			Map<String,String> object = (Map<String, String>) new JSONParser().parse(in, factory);
			object.forEach(api::SetValue);
		} catch (ParseException e) {
			log("Parser", e);
		}
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
