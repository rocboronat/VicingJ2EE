package cat.pipo.bicing.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cat.pipo.bicing.Globals;
import cat.pipo.bicing.cities.canada.montreal.MontrealThread;
import cat.pipo.bicing.cities.spain.barcelona.BarcelonaThread;
import cat.pipo.bicing.cities.spain.girona.GironaThread;
import cat.pipo.bicing.cities.spain.granollers.GranollersThread;
import cat.pipo.bicing.cities.spain.santvicens.SantVicensThread;

/**
 * Servlet Class
 *
 * @web.servlet              name="Main"
 *                           display-name="Name for Main"
 *                           description="Description for Main"
 * @web.servlet-mapping      url-pattern="/Main"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class MainServlet extends HttpServlet {

	@Override
	public void destroy() {
		super.destroy();

		Globals.THREADS_RUNNING = false;
		try {
			Thread.sleep(60000); //Esperem un minutet...
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BarcelonaThread st = new BarcelonaThread();
		st.setName("Thread d'accès a la web del Bicing");
		st.start();

		GironaThread gt = new GironaThread();
		gt.setName("Thread d'accès a la web de la Girocleta");
		gt.start();

		GranollersThread grat = new GranollersThread();
		grat.setName("Thread d'accès a la web de Granollers");
		grat.start();

		MontrealThread mt = new MontrealThread();
		mt.setName("Thread d'accès a la web de Montreal");
		mt.start();

		SantVicensThread svt = new SantVicensThread();
		svt.setName("Thread d'accès a la web de Sant Vicens (manelizzard)");
		svt.start();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw new ServletException("Random smiling monster violated. Protected zone. Who are you? I'm a flying puppet.");
	}
}
