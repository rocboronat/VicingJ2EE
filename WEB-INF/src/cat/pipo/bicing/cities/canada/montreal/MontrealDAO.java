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

package cat.pipo.bicing.cities.canada.montreal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;
import cat.pipo.bicing.cities.DAO;

/** Classe per a recuperar l'HTML de la web de Montreal, parsejar-lo, i crear Beans Station.
 *
 *	@date 14/04/2010
 *	@author Roc Boronat
 *	@see http://rocboronat.net
 */
public class MontrealDAO extends DAO {

	public static boolean loadStations() throws Exception {

		ArrayList<Station> stations = new ArrayList<Station>();
		Map<Short, StationTiny> stationsTiny = new HashMap<Short, StationTiny>();
//		Chrono c = new Chrono();
//		c.start(1);

		boolean primeraVegada = false;

		try {
			synchronized (MontrealGlobals.SEMAFOR) {
					try{

						// Creamos el builder basado en SAX
						SAXBuilder builder = new SAXBuilder();
						// Construimos el arbol DOM a partir del fichero xml#
						Document documentJDOM = builder.build(new URL("https://montreal.bixi.com/data/bikeStations.xml"));

						// Obtenemos la etiqueta raíz
						Element raiz = documentJDOM.getRootElement();
						// Recorremos los hijos de la etiqueta raíz
						List<Element> stationListXML = raiz.getChildren();
						for (Element stationXML : stationListXML) {

							Short id = new Short(stationXML.getChildText("id"));
							String bicis = stationXML.getChildText("nbBikes");
							String lliures = stationXML.getChildText("nbEmptyDocks");

							Station s = loadStation(MontrealGlobals.STATIONS, id);
							if (s!=null){
								s.setForatsPlens(new Byte(bicis));
								s.setForatsBuits(new Byte(lliures));
							} else {
								primeraVegada = true;
								s = new Station(id, stationXML.getChildText("name"), new Byte(bicis), new Byte(lliures),
										new Double( stationXML.getChildText("long")),
										new Double(stationXML.getChildText("lat")));
								repairStationName(s);
								MontrealGlobals.STATIONS.add(s);
							}

							StationTiny st = loadStationTiny(MontrealGlobals.STATIONS_TINY, id);
							if (st!=null){
								st.setForatsPlens(new Byte(bicis));
								st.setForatsBuits(new Byte(lliures));
							} else {
								st = new StationTiny(s);
								MontrealGlobals.STATIONS_TINY.put(id, st);
							}
						}

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
			throw e;
		}

		if (primeraVegada){
			Collections.sort(MontrealGlobals.STATIONS);
		}
		return true;
	}

	private static String treureComes(String s){
		return s.replaceAll(",", "");
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
		} catch (Exception e) {
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

	/** A method to repair the wrong original street names
	 *
	 * @param s The station that we are going to repair
	 */
	private static void repairStationName(Station s){
		String name = s.getDireccio();
		s.setDireccio(name
           .replaceAll("Avenue ", "Av. ")
           .replaceAll("Saint ", "St ")
           .replaceAll("Saint-", "St-")
           .replaceAll("Sainte-", "St-")
           .replaceAll("de ", "")
           .replaceAll("la ", "")
           .replaceAll("des ", "")
           .replaceAll("du ", "")
        );
 }
}
