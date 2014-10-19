package cat.pipo.bicing.cities.canada.montreal.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cat.pipo.bicing.cities.canada.montreal.MontrealGlobals;


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
public class MontrealBinaryServlet extends HttpServlet {

	private static final long serialVersionUID = 1595075018687342206L;

	public MontrealBinaryServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// TODO Auto-generated method stub
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object stations = null;
		synchronized (MontrealGlobals.SEMAFOR) {
			if (req.getParameter("all")!=null){
				stations = MontrealGlobals.STATIONS.clone();
			} else {
				stations = MontrealGlobals.STATIONS_TINY.clone();
			}
		}
		ServletOutputStream sos = resp.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(sos);
		oos.writeObject(stations);
		oos.flush();
		oos.close();
	}
}
