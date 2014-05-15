package cat.pipo.bicing.cities.spain.santvicens.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cat.pipo.bicing.cities.spain.santvicens.SantVicensGlobals;


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
public class SantVicensGzipServlet extends HttpServlet {

	private static final long serialVersionUID = 1595075018687342206L;

	public SantVicensGzipServlet() {
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// TODO Auto-generated method stub
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object stations = null;
		synchronized (SantVicensGlobals.SEMAFOR) {
			if (req.getParameter("all")!=null){
				stations = SantVicensGlobals.STATIONS.clone();
			} else {
				stations = SantVicensGlobals.STATIONS_TINY.clone();
			}
		}
		ServletOutputStream sos = resp.getOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(sos);
		ObjectOutputStream oos = new ObjectOutputStream(gos);
		oos.writeObject(stations);
		oos.flush();
		oos.close();
	}
}
