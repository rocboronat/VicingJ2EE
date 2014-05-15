package cat.pipo.bicing.cities.spain.santvicens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;
import cat.pipo.bicing.cities.DAO;
import cat.pipo.errorLogger.api.SimpleLogThread;

public class SantVicensDAO extends DAO {

	private static final String HTML_TAG = "<[^>]*>";

	private static final String DIR = "APARCAMIENTO";
	private static final String DIREND = "POSICION";

	private static final String BIKESINFO = "ESTADO - (";
	private static final String BIKESINFOEND = ")";

	public static boolean loadStations() throws Exception {
		ArrayList<Station> stations = new ArrayList<Station>();

		try {
			String html = conexionGET("http://www.bicisanvi.es/estado/EstadoActual.asp");
			String no_tags = "";

			if(html!=null && !html.equals("")) {
				no_tags = html.replaceAll(HTML_TAG,"");	// Elimina etiquetas HTML

				Short id = 0;
				// Obtiene la dirección (puede haber direcciones del estilo: PLACE - SUBPLACE
				String direccion = no_tags.substring(no_tags.indexOf(DIR)+DIR.length(),no_tags.indexOf(DIREND));
				//direccion = direccion.substring(0,direccion.lastIndexOf("-")-1);
				// Obtiene un String con formato n/m - n=bicis, m=huecos
				String infoStation = no_tags.substring(no_tags.indexOf(BIKESINFO)+BIKESINFO.length(),no_tags.indexOf(BIKESINFOEND));

				// Elimina del string principal lo ya parseado
				no_tags = no_tags.substring(no_tags.indexOf(BIKESINFOEND)+1, no_tags.length());

				while(no_tags.indexOf(DIR) > -1 && no_tags.indexOf(BIKESINFO) > -1) {
					// Pasa las strings a bytes
					Byte bikes,holes;
					if(infoStation!="") {
						bikes = Byte.parseByte(infoStation.substring(0,infoStation.indexOf("/")));
						holes = Byte.parseByte(infoStation.substring(infoStation.indexOf("/")+1,infoStation.length()));
					} else {
						bikes = holes = 0;
					}

					// Crea una nueva estación (las coordenadas no las proporciona la web)
					Station s = new Station(id++,direccion,bikes,holes,0.0,0.0);
					stations.add(s);


					// Obtiene los datos de la siguiente estación
					direccion = no_tags.substring(no_tags.indexOf(DIR)+DIR.length(),no_tags.indexOf(DIREND));
					//direccion = direccion.substring(0,direccion.lastIndexOf("-")-1);
					infoStation = no_tags.substring(no_tags.indexOf(BIKESINFO)+BIKESINFO.length(),no_tags.indexOf(BIKESINFOEND));
					no_tags = no_tags.substring(no_tags.indexOf(BIKESINFOEND)+1, no_tags.length());
				}
				Byte bikes = Byte.parseByte(infoStation.substring(0,infoStation.indexOf("/")));
				Byte holes = Byte.parseByte(infoStation.substring(infoStation.indexOf("/")+1,infoStation.length()));

				Station s = new Station(id++,direccion,bikes,holes,0.0,0.0);
				stations.add(s);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			SimpleLogThread.send(e,0L,"Error Barcelona DAO");
			throw e;
		}

		SantVicensGlobals.STATIONS = stations;
		HashMap stationsTiny = new HashMap();
		for (Iterator iter = stations.iterator(); iter.hasNext();) {
			Station s = (Station) iter.next();
			stationsTiny.put(s.getId(), new StationTiny(s));
		}
		SantVicensGlobals.STATIONS_TINY = stationsTiny;

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
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),Charset.forName("latin1")));

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
