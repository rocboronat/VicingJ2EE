/*
    Copyright 2010 Roc Boronat

    This file is part of Barcelona Bicing.

    Barcelona Bicing is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Barcelona Bicing is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Barcelona Bicing.  If not, see <http://www.gnu.org/licenses/>.
*/

package cat.pipo.bicing.cities.spain.girona;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;
import cat.pipo.bicing.cities.DAO;
import cat.pipo.errorLogger.api.SimpleLogThread;

/** Classe per a recuperar l'HTML de la web de la Girocleta, parsejar-lo, i crear Beans Station.
 *
 *	@date 14/04/2010
 *	@author Roc Boronat
 *	@see http://rocboronat.net
 */
public class GironaDAO extends DAO {

	private static final String FROM = "map.addControl(mapControl);";
	private static final String TO = "map.setMapType(G_NORMAL_MAP);}}";

	public static boolean loadStations() throws Exception {

		ArrayList<Station> stations = new ArrayList<Station>();
		Map<Short, StationTiny> stationsTiny = new HashMap<Short, StationTiny>();
//		Chrono c = new Chrono();
//		c.start(1);

		boolean primeraVegada = false;

		try {
			String html = conexionGET("http://www.girocleta.cat/Mapadestacions.aspx");
			//System.out.println("Petició GET: " + c.stop(1) + "ms");

			synchronized (GironaGlobals.SEMAFOR) {
				if (html!=null && !html.equals("")){

					String htmlInteressant = html.substring( (html.indexOf(FROM)), html.indexOf(TO)+TO.length());

					int i = 1;
					boolean sacabo = false;

					//c.start(2);

					while (!sacabo){

						String htmlEstacioInici = html.substring( html.indexOf("var html"+i+" ='<div style=\\\"width:210px; padding-right:10px;\\\"><div style=\\\"font-weight:bold;font-size:14px;\\\">")
								+ (("var html"+i+" ='<div style=\\\"width:210px; padding-right:10px;\\\"><div style=\\\"font-weight:bold;font-size:14px;\\\">").length()));
						String htmlEstacioInteressant = htmlEstacioInici.substring(0, htmlEstacioInici.indexOf("</div></div>"));

						String id = htmlEstacioInteressant.substring(0, htmlEstacioInteressant.indexOf("-"));
						String direccio = htmlEstacioInteressant.substring(htmlEstacioInteressant.indexOf("-")+1, htmlEstacioInteressant.indexOf("</div>")).trim();

						String bicisLliuresBrut = htmlEstacioInteressant.substring(htmlEstacioInteressant.indexOf("Bicis lliures: ")+"Bicis lliures: ".length());
						String bicisLliures = bicisLliuresBrut.substring(0, bicisLliuresBrut.indexOf("</div>")).trim();
						String aparcamentsLliures = htmlEstacioInteressant.substring(htmlEstacioInteressant.indexOf("Aparcaments lliures: ")+"Aparcaments lliures: ".length());

						String coordsBrut = htmlInteressant.substring(htmlInteressant.indexOf("var pParking"+i+"= new GLatLng(")+("var pParking"+i+"= new GLatLng(").length());
						String coordsNet = coordsBrut.substring(0, coordsBrut.indexOf(");"));

						double y = new Double(coordsNet.substring(0, coordsNet.indexOf(",")));
						double x = new Double(coordsNet.substring(coordsNet.indexOf(",")+1));

						Station s = loadStation(GironaGlobals.STATIONS, new Short(id));
						if (s!=null){
							s.setForatsPlens(new Byte(bicisLliures));
							s.setForatsBuits(new Byte(aparcamentsLliures));
						} else {
							primeraVegada = true;
							s = new Station(new Short(id), direccio, new Byte(bicisLliures), new Byte(aparcamentsLliures), x, y);
							GironaGlobals.STATIONS.add(s);
						}

						StationTiny st = loadStationTiny(GironaGlobals.STATIONS_TINY, new Short(id));
						if (st!=null){
							st.setForatsPlens(new Byte(bicisLliures));
							st.setForatsBuits(new Byte(aparcamentsLliures));
						} else {
							st = new StationTiny(s);
							GironaGlobals.STATIONS_TINY.put(new Short(id), st);
						}



						i++;
						if (html.indexOf("var html"+ i +" ='<div style=\\\"width:210px; padding-right:10px;\\\"><div style=\\\"font-weight:bold;font-size:14px;\\\">") == -1)
							sacabo=true;

					}
	//
					//System.out.println("Iterar Document: " + c.stop(2) + "ms");
				}
			}
		} catch (Exception e) {
			SimpleLogThread.send(e,0L,"Error Girona DAO");
			throw e;
		}

		if (primeraVegada){
			Collections.sort(GironaGlobals.STATIONS);
		}
		return true;
	}


	private static String conexionGET(String u) {
		StringBuilder resp = new StringBuilder();
		BufferedReader rd = null;

		try {
			URL url = new URL(u);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),Charset.forName("UTF-8")));

			String line;
			while ((line = rd.readLine()) != null) {
				// Process line...
				resp.append(line);
			}
		} catch (MalformedURLException e) {
			SimpleLogThread.send(e, 0L, "ERROR URL");
			return null;
		} catch (SocketTimeoutException e) {
			SimpleLogThread.send(e, 0L, "ERROR TIMEOUT");
			return null;
		} catch (IOException e) {
			SimpleLogThread.send(e, 0L, "ERROR IO");
			return null;
		}  catch (Exception e) {
			SimpleLogThread.send(e, 0L, "ERROR");
			return null;
		} finally {
			if (rd != null) {
				try {
					rd.close();
					rd = null;
				} catch (IOException ex) {
					System.out.println("Cannot close, man!");
					rd = null;
				}
			}
		}
		return resp.toString();
	}
}
