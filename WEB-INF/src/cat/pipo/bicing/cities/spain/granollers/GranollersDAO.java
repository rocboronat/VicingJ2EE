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

package cat.pipo.bicing.cities.spain.granollers;
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
import java.util.StringTokenizer;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;
import cat.pipo.bicing.cities.DAO;
import cat.pipo.errorLogger.api.SimpleLogThread;

/** Classe per a recuperar l'HTML de la web de Granollers, parsejar-lo, i crear Beans Station.
 *
 *	@date 14/04/2010
 *	@author Roc Boronat
 *	@see http://rocboronat.net
 */
public class GranollersDAO extends DAO {

	private static final String FROM = "<markers>";
	private static final String TO = "</markers>";

	private static final String FROM_DIRECCIO = "<marker nombre=\"";
	private static final String TO_DIRECCIO = "\" latitud=\"";

	private static final String FROM_LATITUD = "latitud=\"";
	private static final String TO_LATITUD = "\" longitud=\"";

	private static final String FROM_LONGITUD = "longitud=\"";
	private static final String TO_LONGITUD = "\" libres=\"";

	private static final String FROM_LIBRES = "libres=\"";
	private static final String TO_LIBRES = "\" ocupadas=\"";

	private static final String FROM_OCUPADAS = "ocupadas=\"";
	private static final String TO_OCUPADAS = "\" /";

	public static boolean loadStations() throws Exception {

		ArrayList<Station> stations = new ArrayList<Station>();
		Map<Short, StationTiny> stationsTiny = new HashMap<Short, StationTiny>();
//		Chrono c = new Chrono();
//		c.start(1);

		boolean primeraVegada = false;

		try {
			String xmlGran = conexionGET("http://ambiciat.granollers.cat/ambiciat/estaciones/estacionesXML.aspx?dis=false&lib=false");

			synchronized (GranollersGlobals.SEMAFOR) {
				if (xmlGran!=null && !xmlGran.equals("")){

					xmlGran = xmlGran.substring( (xmlGran.indexOf(FROM)+FROM.length()), xmlGran.indexOf(TO));
					boolean sacabo = false;

					//c.start(2);

					StringTokenizer tokenizer = new StringTokenizer(xmlGran, ">");
					while (tokenizer.hasMoreTokens()){
						String xml = tokenizer.nextToken();
//						System.out.println(xml);

						String latitud = xml.substring( (xml.indexOf(FROM_LATITUD)+FROM_LATITUD.length()), xml.indexOf(TO_LATITUD));
						String longitud = xml.substring( (xml.indexOf(FROM_LONGITUD)+FROM_LONGITUD.length()), xml.indexOf(TO_LONGITUD));
						String libres = xml.substring( (xml.indexOf(FROM_LIBRES)+FROM_LIBRES.length()), xml.indexOf(TO_LIBRES));
						String ocupades = xml.substring( (xml.indexOf(FROM_OCUPADAS)+FROM_OCUPADAS.length()), xml.indexOf(TO_OCUPADAS));

						latitud = treureComes(latitud);
						longitud = treureComes(longitud);

						if (latitud.length()>4){ //si la estació no té punt GPS, com la de Plaça Maluquer i Salvador ...
							Short id = new Short(latitud.substring(latitud.length()-3, latitud.length()));

							Station s = loadStation(GranollersGlobals.STATIONS, id);
							if (s!=null){
								s.setForatsPlens(new Byte(ocupades));
								s.setForatsBuits(new Byte(libres));
							} else {
								primeraVegada = true;
								String direccio = xml.substring( (xml.indexOf(FROM_DIRECCIO)+FROM_DIRECCIO.length()), xml.indexOf(TO_DIRECCIO));
								s = new Station(id, direccio, new Byte(ocupades), new Byte(libres), new Double(longitud), new Double(latitud));
								repairStationName(s);
								GranollersGlobals.STATIONS.add(s);
							}

							StationTiny st = loadStationTiny(GranollersGlobals.STATIONS_TINY, id);
							if (st!=null){
								st.setForatsPlens(new Byte(ocupades));
								st.setForatsBuits(new Byte(libres));
							} else {
								st = new StationTiny(s);
								GranollersGlobals.STATIONS_TINY.put(id, st);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			SimpleLogThread.send(e,0L,"Error Granollers DAO");
			throw e;
		}

		if (primeraVegada){
			Collections.sort(GranollersGlobals.STATIONS);
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

	/** A method to repair the wrong original street names
	 *
	 * @param s The station that we are going to repair
	 */
	private static void repairStationName(Station s){
		String name = s.getDireccio();
		s.setDireccio(name
            .replaceAll("Carrer ", "")
            .replaceAll("Avinguda ", "Av. ")
            .replaceAll("Passeig ", "Psg. ")
            .replaceAll("Plaça ", "Pl. ")
         );
  }

}
