package cat.pipo.bicing.cities.spain.girona.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cat.pipo.bicing.cities.spain.girona.GironaGlobals;

import com.google.gson.Gson;


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
public class GironaJsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1595075018687342206L;

	public GironaJsonServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// TODO Auto-generated method stub
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object stations = null;
		synchronized (GironaGlobals.SEMAFOR) {
			if (req.getParameter("all")!=null){
				stations = GironaGlobals.STATIONS.clone();
			} else {
				stations = GironaGlobals.STATIONS_TINY.clone();
			}
		}

		Gson gson = new Gson();
		String jsonOutput = gson.toJson(stations);

		ServletOutputStream ws = resp.getOutputStream();
		ws.print(jsonOutput);
		ws.flush();
		ws.close();
	}
}
