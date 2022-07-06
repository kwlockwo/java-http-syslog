import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.github.palindromicity.syslog.SyslogParser;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;

public class Main extends HttpServlet {

	private static final Logger logger = LogManager.getLogger(Main.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		logger.debug("Debug Message");
		logger.error("Error Message");
		logger.warn("Warn message");
		logger.info("Info message");

		res.getWriter().print("Check your logs");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		/* StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (Exception ex) {}
		
		System.out.println(sb.toString()); */

		List<Map<String,Object>> syslogMapList = null;
		SyslogParser parser = new SyslogParserBuilder().forSpecification(SyslogSpecification.HEROKU_HTTPS_LOG_DRAIN).build();
		try (BufferedReader reader = req.getReader()) {
			syslogMapList = parser.parseLines(reader);
		}

		for(Map<String,Object> syslog : syslogMapList) {
			System.out.println(syslog);
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(Integer.valueOf(System.getenv("PORT")));
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new Main()), "/*");
		server.start();
		server.join();
	}
}
