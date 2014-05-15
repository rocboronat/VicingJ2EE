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

package cat.pipo.bicing.cities.spain.barcelona;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;
import cat.pipo.bicing.cities.DAO;
import cat.pipo.bicing.cities.spain.barcelona.util.StationNavigationDataSet;
import cat.pipo.errorLogger.api.SimpleLogThread;

/**
 * Classe per a recuperar l'HTML de la web del Bicing, parsejar-lo, i crear Beans Station.
 * 
 * @date 14/04/2010
 * @author Roc Boronat
 * @see http://rocboronat.net
 */
public class BarcelonaDAO extends DAO {

	private static final Short MERIDIANA274 = 274;
	private static final Short SANCHOAVILA = 143;
	private static final Short DOCTOR_ROUX = 332;

	private static boolean primeraVegada = false;

	public static boolean loadStations() throws Exception {
		// Chrono c = new Chrono();
		// c.start(1);

		try {
			String response = conexionGET("http://wservice.viabicing.cat/getstations.php?v=1");
			StationNavigationDataSet snds = StationNavigationDataSet.getStationsNavigationDataSet(response);
			List<Station> stations = snds.getStations();

			for (Station s : stations) {
				if (!BarcelonaGlobals.STATIONS.contains(s)) {
					primeraVegada = true;
					repairStationName(s);

					if (s.getId().equals(MERIDIANA274)) {
						s.setY(41.43025);
						s.setX(2.185033);
					} else if (s.getId().equals(SANCHOAVILA)) {
						s.setY(41.403);
						s.setX(2.1958);
					} else if (s.getId().equals(DOCTOR_ROUX)) {
						s.setY(41.399803);
						s.setX(2.128145);
					}

					BarcelonaGlobals.STATIONS.add(s);

					StationTiny st = new StationTiny(s);
					BarcelonaGlobals.STATIONS_TINY.put(s.getId(), st);
				} else {
					primeraVegada = false;
					Station oldStation = loadStation(BarcelonaGlobals.STATIONS, s.getId());
					oldStation.setForatsBuits(s.getForatsBuits());
					oldStation.setForatsPlens(s.getForatsPlens());

					StationTiny st = loadStationTiny(BarcelonaGlobals.STATIONS_TINY, s.getId());
					st.setForatsBuits(s.getForatsBuits());
					st.setForatsPlens(s.getForatsPlens());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			SimpleLogThread.send(e, 0L, "Error Barcelona DAO");
			throw e;
		}

		if (primeraVegada) {
			Collections.sort(BarcelonaGlobals.STATIONS);
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
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("latin1")));

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
		} catch (Exception e) {
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

	/**
	 * A method to repair the wrong original street names
	 * 
	 * @param s
	 *            The station that we are going to repair
	 */
	private static void repairStationName(Station s) {
		String name = s.getDireccio();
		s.setDireccio(name.replaceAll("Av. Del Litoral", "Av. Litoral").replaceAll("Ada. ", "Av. ").replaceAll("Av ", "Av. ").replaceAll("Avda. ", "Av. ").replaceAll("Avinguda ", "Av. ").replaceAll("Avda ", "Av. ")
				.replaceAll("Diagonal", "Av. Diagonal").replaceAll("Meridiana", "Av. Meridiana").replaceAll("Av. Av. ", "Av. ").replaceAll("Arago", "Aragó").replaceAll("Ali Bei", "Alí Bei").replaceAll("Carrer de la ", "")
				.replaceAll("Carrer de les ", "").replaceAll("Carrer dels ", "").replaceAll("Carrer del ", "").replaceAll("Carrer de ", "").replaceAll("Carrer ", "").replaceAll("Carre ", "").replaceAll("Doctor", "Dr.")
				.replaceAll("Gran Via Corts Catalanes", "Gran Via").replaceAll("Gran Via de Les Corts Catalanes", "Gran Via").replaceAll("Gran Via, 361", "Gran Via").replaceAll("Gran Vía, 1041/ Selva de Mar", "Gran Via")
				.replaceAll("Gran Vía", "Gran Via").replaceAll("Napols", "Nàpols").replaceAll("Marquès de l", "Marquès d").replaceAll("Passeig marítim de la mar bella", "Passeig Marítim de la Mar Bella")
				.replaceAll("Pg marítim barceloneta", "Passeig Marítim de la Barceloneta").replaceAll("Passeig lluís companys", "Passeig de Lluís Companys").replaceAll("Passeig", "Psg.").replaceAll("Pg ", "Psg. ")
				.replaceAll("P. Fabra i Puig", "Psg. Fabra i Puig").replaceAll("Pl. Antonio Lopez (costat Pg. de Colom) annexa a la 37", "Pl. Antonio López (Pg. de Colom)")
				.replaceAll("Pl. Antonio Lopez (Via Laietana) annexa a la 37", "Pl. Antonio López (Via Laietana)").replaceAll("Pl.Joaquim", "Pl. Joaquim").replaceAll("Plaza", "Pl.").replaceAll("Plaça", "Pl.").replaceAll("PL.", "Pl.")
				.replaceAll("Ronda", "Rda.").replaceAll("Rambla de Prim", "Rambla Prim").replaceAll("Del Nord", "del Nord").replaceAll("Rambla Catalunya, 31 ( Diputació)", "Rambla Catalunya").replaceAll("Guinardo", "Guinardó")
				.replaceAll("Rossello", "Rosselló").replaceAll("Vilamarí davant", "Vilamarí").replaceAll("C. Del ", "").replaceAll("C. De ", "").replaceAll("C/ de ", "").replaceAll("C/", "").replaceAll("Can bruixa", "Can Bruixa")
				.replaceAll("Sant ", "St. ").replaceAll("Asuncion", "Asunción").replaceAll("Dos Maig", "Dos de Maig").replaceAll("Joan d'Austria,50", "Joan d'Austria, 50").replaceAll("Paral·le", "Paral·lel")
				.replaceAll("Paral.lel", "Paral·lel").replaceAll("Paral·lell", "Paral·lel").replaceAll("Pl. Alfons X el Savi / Rda. del Guinardó", "Pl. Alfons X el Savi").replaceAll("(annexa a l'estació 10)", "")
				.replaceAll(", devant del 5-7", "").replaceAll("Pl. de la Trinitat, 5-7 / amb Mare de Déu de Lorda", "Pl. de la Trinitat, 5-7").replaceAll("del mar", "del Mar").replaceAll("annexa a la 37", "")
				.replaceAll("Ramon Turro", "Ramon Turró").replaceAll("Sensat en front", "Sensat").replaceAll("Papaseit", "Papasseit").replaceAll("/ Rda. St. Pau", "").replaceAll("Taquigraf", "Taquígraf")
				.replaceAll("Comte Bell-lloch", "Comtes de Bell-lloc").replaceAll("Figols", "Fígols").replaceAll("Icaria", "Icària").replaceAll("Ausias", "Ausiàs").replaceAll("Compte", "Comte").replaceAll("Rambla ", "Rbla. "));

		if (2 == 1) {
			// foo...
			// } else if (s.getId().equals(new Short("0"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("1"))) {
			s.setDireccio("Gran Via, 758");
		} else if (s.getId().equals(new Short("2"))) {
			s.setDireccio("Pl. Tetuan, 9");
		} else if (s.getId().equals(new Short("3"))) {
			s.setDireccio("Alí Bei, 44");
		} else if (s.getId().equals(new Short("4"))) {
			s.setDireccio("Ribes, 11");
		} else if (s.getId().equals(new Short("5"))) {
			s.setDireccio("Arc de Triomf");
		} else if (s.getId().equals(new Short("6"))) {
			s.setDireccio("Arc de Triomf");
		} else if (s.getId().equals(new Short("7"))) {
			s.setDireccio("Pujades (Ciutadella)");
		} else if (s.getId().equals(new Short("8"))) {
			s.setDireccio("Pujades (Ciutadella)");
		} else if (s.getId().equals(new Short("9"))) {
			s.setDireccio("Av. Marquès Argentera, 21");
		} else if (s.getId().equals(new Short("10"))) {
			s.setDireccio("Pl. Comercial");
		} else if (s.getId().equals(new Short("11"))) {
			s.setDireccio("Psg. Marítim (Brugada)");
		} else if (s.getId().equals(new Short("12"))) {
			s.setDireccio("Psg. Marítim (Hospital del Mar)");
		} else if (s.getId().equals(new Short("13"))) {
			s.setDireccio("Av. Litoral, 14");
		} else if (s.getId().equals(new Short("14"))) {
			s.setDireccio("Av. Marquès Argentera, 19");
		} else if (s.getId().equals(new Short("15"))) {
			s.setDireccio("Girona, 74");
		} else if (s.getId().equals(new Short("16"))) {
			s.setDireccio("Av. Meridiana, 47");
		} else if (s.getId().equals(new Short("17"))) {
			s.setDireccio("Av. Meridiana, 47");
		} else if (s.getId().equals(new Short("18"))) {
			s.setDireccio("Rosselló, 404");
		} else if (s.getId().equals(new Short("19"))) {
			s.setDireccio("Rosselló, 352");
		} else if (s.getId().equals(new Short("20"))) {
			s.setDireccio("Indústria, 157");
		} else if (s.getId().equals(new Short("21"))) {
			s.setDireccio("St. Antoni Mª Claret, 214");
		} else if (s.getId().equals(new Short("22"))) {
			s.setDireccio("Sardenya, 296");
		} else if (s.getId().equals(new Short("23"))) {
			s.setDireccio("Bruc, 47");
		} else if (s.getId().equals(new Short("24"))) {
			s.setDireccio("Marina, 190");
		} else if (s.getId().equals(new Short("25"))) {
			s.setDireccio("Bruc, 87");
		} else if (s.getId().equals(new Short("26"))) {
			s.setDireccio("Dos de Maig, 230");
		} else if (s.getId().equals(new Short("27"))) {
			s.setDireccio("Provença, 324");
		} else if (s.getId().equals(new Short("28"))) {
			s.setDireccio("Marina, 344");
		} else if (s.getId().equals(new Short("29"))) {
			s.setDireccio("Provença, 388");
		} else if (s.getId().equals(new Short("30"))) {
			s.setDireccio("Av. Diagonal, 304");
		} else if (s.getId().equals(new Short("31"))) {
			s.setDireccio("Psg. Joan de Borbó, 72");
		} else if (s.getId().equals(new Short("32"))) {
			s.setDireccio("Psg. Marítim (Port Vell)");
		} else if (s.getId().equals(new Short("33"))) {
			s.setDireccio("Baluart, 58");
		} else if (s.getId().equals(new Short("34"))) {
			s.setDireccio("St. Pere més Alt, 4");
		} else if (s.getId().equals(new Short("35"))) {
			s.setDireccio("Ramon Berenguer");
		} else if (s.getId().equals(new Short("36"))) {
			s.setDireccio("Av. Catedral, 6");
		} else if (s.getId().equals(new Short("37"))) {
			s.setDireccio("Psg. de Colom");
		} else if (s.getId().equals(new Short("38"))) {
			s.setDireccio("Pl. Pau Vila");
		} else if (s.getId().equals(new Short("39"))) {
			s.setDireccio("Pl. Pau Vila");
		} else if (s.getId().equals(new Short("40"))) {
			s.setDireccio("Dr. Aiguader, 2");
		} else if (s.getId().equals(new Short("41"))) {
			s.setDireccio("Pl. Poeta Boscà");
		} else if (s.getId().equals(new Short("42"))) {
			s.setDireccio("Ciutat de Granada, 168");
		} else if (s.getId().equals(new Short("43"))) {
			s.setDireccio("Av. Meridiana, 68");
		} else if (s.getId().equals(new Short("44"))) {
			s.setDireccio("Av. Meridiana, 66");
		} else if (s.getId().equals(new Short("45"))) {
			s.setDireccio("Marina, 61");
		} else if (s.getId().equals(new Short("46"))) {
			s.setDireccio("Ramon Trias Fargas, 19");
		} else if (s.getId().equals(new Short("47"))) {
			s.setDireccio("Ramon Trias Fargas, 23");
		} else if (s.getId().equals(new Short("48"))) {
			s.setDireccio("Av. Meridiana, 30");
		} else if (s.getId().equals(new Short("49"))) {
			s.setDireccio("Rosa Sensat, 14");
		} else if (s.getId().equals(new Short("50"))) {
			s.setDireccio("Paral·lel, 54");
		} else if (s.getId().equals(new Short("51"))) {
			s.setDireccio("Pl. Vicenç Martorell");
		} else if (s.getId().equals(new Short("52"))) {
			s.setDireccio("Pl. Sant Miquel");
		} else if (s.getId().equals(new Short("53"))) {
			s.setDireccio("Pl. Carles Pi i Sunyer");
		} else if (s.getId().equals(new Short("54"))) {
			s.setDireccio("St. Oleguer, 16");
		} else if (s.getId().equals(new Short("55"))) {
			s.setDireccio("La Rambla, 80");
		} else if (s.getId().equals(new Short("56"))) {
			s.setDireccio("Sta. Madrona");
		} else if (s.getId().equals(new Short("57"))) {
			s.setDireccio("La Rambla, 2");
		} else if (s.getId().equals(new Short("58"))) {
			s.setDireccio("Pl. dels Àngels (MACBA)");
		} else if (s.getId().equals(new Short("59"))) {
			s.setDireccio("Pl. dels Àngels (MACBA)");
		} else if (s.getId().equals(new Short("60"))) {
			s.setDireccio("Rmbl. Catalunya, 45");
		} else if (s.getId().equals(new Short("61"))) {
			s.setDireccio("Rmbl. Catalunya, 40");
		} else if (s.getId().equals(new Short("62"))) {
			s.setDireccio("Pl. Catalunya (Rda. Universitat)");
		} else if (s.getId().equals(new Short("63"))) {
			s.setDireccio("Pl. Catalunya (C/ Bergara)");
		} else if (s.getId().equals(new Short("64"))) {
			s.setDireccio("Pl. Catalunya (Rda. Universitat)");
		} else if (s.getId().equals(new Short("65"))) {
			s.setDireccio("Pl. Catalunya (Rda. Universitat)");
		} else if (s.getId().equals(new Short("66"))) {
			s.setDireccio("Gran Via, 630");
		} else if (s.getId().equals(new Short("67"))) {
			s.setDireccio("Rocafort, 212");
		} else if (s.getId().equals(new Short("68"))) {
			s.setDireccio("Rmbl. Catalunya, 131");
		} else if (s.getId().equals(new Short("69"))) {
			s.setDireccio("Av. Litoral, 14");
		} else if (s.getId().equals(new Short("70"))) {
			s.setDireccio("Villarroel, 21");
		} else if (s.getId().equals(new Short("71"))) {
			s.setDireccio("Floridablanca, 145");
		} else if (s.getId().equals(new Short("72"))) {
			s.setDireccio("Rosselló, 217");
		} else if (s.getId().equals(new Short("73"))) {
			s.setDireccio("Enric Granados, 99");
		} else if (s.getId().equals(new Short("74"))) {
			s.setDireccio("Av. Josep Tarradellas, 123");
		} else if (s.getId().equals(new Short("75"))) {
			s.setDireccio("Av. Josep Tarradellas, 58");
		} else if (s.getId().equals(new Short("76"))) {
			s.setDireccio("Còrsega, 214");
			// } else if (s.getId().equals(new Short("77"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("78"))) {
			s.setDireccio("Pl. Universitat");
		} else if (s.getId().equals(new Short("79"))) {
			s.setDireccio("Pl. Universitat");
		} else if (s.getId().equals(new Short("80"))) {
			s.setDireccio("Enric Granados, 37");
		} else if (s.getId().equals(new Short("81"))) {
			s.setDireccio("Parc de Joan Miró");
		} else if (s.getId().equals(new Short("82"))) {
			s.setDireccio("Rocafort, 72");
		} else if (s.getId().equals(new Short("83"))) {
			s.setDireccio("Comte Borrell, 177");
		} else if (s.getId().equals(new Short("84"))) {
			s.setDireccio("Diputació, 137");
		} else if (s.getId().equals(new Short("85"))) {
			s.setDireccio("Paral·lel, 144");
		} else if (s.getId().equals(new Short("86"))) {
			s.setDireccio("Paral·lel, 114");
		} else if (s.getId().equals(new Short("87"))) {
			s.setDireccio("Mallorca, 41 ");
		} else if (s.getId().equals(new Short("88"))) {
			s.setDireccio("Londres, 92");
		} else if (s.getId().equals(new Short("89"))) {
			s.setDireccio("Rosselló, 101");
		} else if (s.getId().equals(new Short("90"))) {
			s.setDireccio("Rosselló, 108");
		} else if (s.getId().equals(new Short("91"))) {
			s.setDireccio("Comte Borrell, 117");
		} else if (s.getId().equals(new Short("92"))) {
			s.setDireccio("Provença, 276");
		} else if (s.getId().equals(new Short("93"))) {
			s.setDireccio("Gran Via, 384");
		} else if (s.getId().equals(new Short("94"))) {
			s.setDireccio("Gran Via, 384");
		} else if (s.getId().equals(new Short("95"))) {
			s.setDireccio("Tarragona, 109");
		} else if (s.getId().equals(new Short("96"))) {
			s.setDireccio("Gran Via, 326");
		} else if (s.getId().equals(new Short("97"))) {
			s.setDireccio("Tarragona, 141");
		} else if (s.getId().equals(new Short("98"))) {
			s.setDireccio("Viriat, 45");
		} else if (s.getId().equals(new Short("99"))) {
			s.setDireccio("Viriat, 53");
		} else if (s.getId().equals(new Short("100"))) {
			s.setDireccio("Tarragona, 159");
		} else if (s.getId().equals(new Short("101"))) {
			s.setDireccio("Av. Pau Casals, 1");
		} else if (s.getId().equals(new Short("102"))) {
			s.setDireccio("Av. Pau Casals, 3");
		} else if (s.getId().equals(new Short("103"))) {
			s.setDireccio("Aragó, 625");
		} else if (s.getId().equals(new Short("104"))) {
			s.setDireccio("València, 621");
		} else if (s.getId().equals(new Short("105"))) {
			s.setDireccio("Pl. Urquinaona, 9");
		} else if (s.getId().equals(new Short("106"))) {
			s.setDireccio("Pl. Joanic");
		} else if (s.getId().equals(new Short("107"))) {
			s.setDireccio("Travessera de Gràcia, 90");
		} else if (s.getId().equals(new Short("108"))) {
			s.setDireccio("Indústria, 12");
		} else if (s.getId().equals(new Short("109"))) {
			s.setDireccio("Londres, 53");
		} else if (s.getId().equals(new Short("110"))) {
			s.setDireccio("Av. Roma, 117");
		} else if (s.getId().equals(new Short("111"))) {
			s.setDireccio("Calàbria, 135");
		} else if (s.getId().equals(new Short("112"))) {
			s.setDireccio("Floridablanca, 49");
		} else if (s.getId().equals(new Short("113"))) {
			s.setDireccio("Comte Borrell, 62");
		} else if (s.getId().equals(new Short("114"))) {
			s.setDireccio("Pl. Jean Genet");
		} else if (s.getId().equals(new Short("115"))) {
			s.setDireccio("Av. Marquès Argentera, 1");
		} else if (s.getId().equals(new Short("116"))) {
			s.setDireccio("Dr. Aiguader, 72");
		} else if (s.getId().equals(new Short("117"))) {
			s.setDireccio("Rosa Sensat, 6");
		} else if (s.getId().equals(new Short("118"))) {
			s.setDireccio("Pujades, 3");
		} else if (s.getId().equals(new Short("119"))) {
			s.setDireccio("Ausiàs March, 122");
		} else if (s.getId().equals(new Short("120"))) {
			s.setDireccio("Lepant, 278");
		} else if (s.getId().equals(new Short("121"))) {
			s.setDireccio("Castillejos, 258");
		} else if (s.getId().equals(new Short("122"))) {
			s.setDireccio("Nàpols, 344");
		} else if (s.getId().equals(new Short("123"))) {
			s.setDireccio("Girona, 122");
		} else if (s.getId().equals(new Short("124"))) {
			s.setDireccio("Atlantida, 82");
		} else if (s.getId().equals(new Short("125"))) {
			s.setDireccio("Psg. Marítim (Barceloneta)");
		} else if (s.getId().equals(new Short("126"))) {
			s.setDireccio("Psg. de Colom");
		} else if (s.getId().equals(new Short("127"))) {
			s.setDireccio("Rmbl. Guipúscoa, 661");
		} else if (s.getId().equals(new Short("128"))) {
			s.setDireccio("Rmbl. Guipúscoa, 47");
		} else if (s.getId().equals(new Short("129"))) {
			s.setDireccio("Rmbl. Guipúscoa, 69");
		} else if (s.getId().equals(new Short("130"))) {
			s.setDireccio("Rmbl. Guipúscoa, 107");
		} else if (s.getId().equals(new Short("131"))) {
			s.setDireccio("Rmbl. Guipúscoa, 154");
		} else if (s.getId().equals(new Short("132"))) {
			s.setDireccio("Pl. Valentí Almirall");
		} else if (s.getId().equals(new Short("133"))) {
			s.setDireccio("Gran Via, 167");
		} else if (s.getId().equals(new Short("134"))) {
			s.setDireccio("Gran Via, 1002");
		} else if (s.getId().equals(new Short("135"))) {
			s.setDireccio("Gran Via, 1002");
		} else if (s.getId().equals(new Short("136"))) {
			s.setDireccio("Gran Via, 1029");
		} else if (s.getId().equals(new Short("137"))) {
			s.setDireccio("Gran Via, 1060");
		} else if (s.getId().equals(new Short("138"))) {
			s.setDireccio("Gran Via, 1126");
		} else if (s.getId().equals(new Short("139"))) {
			s.setDireccio("Gran Via, 1133");
		} else if (s.getId().equals(new Short("140"))) {
			s.setDireccio("Gran Via, 1176");
		} else if (s.getId().equals(new Short("141"))) {
			s.setDireccio("Gran Via, 938");
		} else if (s.getId().equals(new Short("142"))) {
			s.setDireccio("Sancho de Avila, 104");
		} else if (s.getId().equals(new Short("143"))) {
			s.setDireccio("Sancho de Avila, 152");
		} else if (s.getId().equals(new Short("144"))) {
			s.setDireccio("Castella, 28");
		} else if (s.getId().equals(new Short("145"))) {
			s.setDireccio("Pere IV, 295");
		} else if (s.getId().equals(new Short("146"))) {
			s.setDireccio("Pere IV, 383");
		} else if (s.getId().equals(new Short("147"))) {
			s.setDireccio("Rbla. Prim, 79");
		} else if (s.getId().equals(new Short("148"))) {
			s.setDireccio("Erasme de Janer, 1");
		} else if (s.getId().equals(new Short("149"))) {
			s.setDireccio("Pujades, 57");
		} else if (s.getId().equals(new Short("150"))) {
			s.setDireccio("Espronceda, 115");
		} else if (s.getId().equals(new Short("151"))) {
			s.setDireccio("Pujades, 462");
		} else if (s.getId().equals(new Short("152"))) {
			s.setDireccio("Pujades, 119");
		} else if (s.getId().equals(new Short("153"))) {
			s.setDireccio("Pujades, 186");
		} else if (s.getId().equals(new Short("154"))) {
			s.setDireccio("Pujades, 189");
		} else if (s.getId().equals(new Short("155"))) {
			s.setDireccio("Pujades, 311");
		} else if (s.getId().equals(new Short("156"))) {
			s.setDireccio("Av. Diagonal, 80");
		} else if (s.getId().equals(new Short("157"))) {
			s.setDireccio("Llull, 396");
		} else if (s.getId().equals(new Short("158"))) {
			s.setDireccio("Rbla. Prim, 19");
		} else if (s.getId().equals(new Short("159"))) {
			s.setDireccio("Av. Diagonal, 8");
		} else if (s.getId().equals(new Short("160"))) {
			s.setDireccio("Av. d'Eduard Maristany");
		} else if (s.getId().equals(new Short("161"))) {
			s.setDireccio("Ramon Turró, 93");
		} else if (s.getId().equals(new Short("162"))) {
			s.setDireccio("Ramon Turró, 287");
		} else if (s.getId().equals(new Short("163"))) {
			s.setDireccio("Av. Icària, 202");
		} else if (s.getId().equals(new Short("164"))) {
			s.setDireccio("Independència, 379");
		} else if (s.getId().equals(new Short("165"))) {
			s.setDireccio("Dr. Trueta, 222");
		} else if (s.getId().equals(new Short("166"))) {
			s.setDireccio("Psg. del Taulat, 171");
		} else if (s.getId().equals(new Short("167"))) {
			s.setDireccio("Psg. del Taulat, 158");
		} else if (s.getId().equals(new Short("168"))) {
			s.setDireccio("Psg. del Taulat, 238");
		} else if (s.getId().equals(new Short("169"))) {
			s.setDireccio("Av. Litoral, 24");
		} else if (s.getId().equals(new Short("170"))) {
			s.setDireccio("Av. Litoral, 24");
		} else if (s.getId().equals(new Short("171"))) {
			s.setDireccio("Parc de la Nova Icària");
		} else if (s.getId().equals(new Short("172"))) {
			s.setDireccio("Parc de la Nova Icària");
		} else if (s.getId().equals(new Short("173"))) {
			s.setDireccio("Av. Litoral, 82");
		} else if (s.getId().equals(new Short("174"))) {
			s.setDireccio("Psg. de Garcia Faria, 21");
		} else if (s.getId().equals(new Short("175"))) {
			s.setDireccio("Psg. Marítim (Mar Bella)");
		} else if (s.getId().equals(new Short("176"))) {
			s.setDireccio("Psg. de Garcia Faria, 37");
		} else if (s.getId().equals(new Short("177"))) {
			s.setDireccio("Rosselló, 557");
		} else if (s.getId().equals(new Short("178"))) {
			s.setDireccio("Psg. de Garcia Faria, 85");
		} else if (s.getId().equals(new Short("179"))) {
			s.setDireccio("Rbla. Badal, 2");
		} else if (s.getId().equals(new Short("180"))) {
			s.setDireccio("Gran Via, 183");
		} else if (s.getId().equals(new Short("181"))) {
			s.setDireccio("Gran Via, 183");
		} else if (s.getId().equals(new Short("182"))) {
			s.setDireccio("Gran Via, 273");
		} else if (s.getId().equals(new Short("183"))) {
			s.setDireccio("Gavà, 1");
		} else if (s.getId().equals(new Short("184"))) {
			s.setDireccio("Quetzal, 22");
		} else if (s.getId().equals(new Short("185"))) {
			s.setDireccio("Gavà, 81");
		} else if (s.getId().equals(new Short("186"))) {
			s.setDireccio("Consell de Cent, 4");
		} else if (s.getId().equals(new Short("187"))) {
			s.setDireccio("St. Pau, 89");
		} else if (s.getId().equals(new Short("188"))) {
			s.setDireccio("Joan Güell, 2");
		} else if (s.getId().equals(new Short("189"))) {
			s.setDireccio("Bruc, 134");
		} else if (s.getId().equals(new Short("190"))) {
			s.setDireccio("Av. Litoral (Rambla)");
		} else if (s.getId().equals(new Short("191"))) {
			s.setDireccio("Rocafort, 167");
		} else if (s.getId().equals(new Short("192"))) {
			s.setDireccio("Joan Güell, 89");
		} else if (s.getId().equals(new Short("193"))) {
			s.setDireccio("Fígols, 1");
		} else if (s.getId().equals(new Short("194"))) {
			s.setDireccio("Joan Güell, 139");
		} else if (s.getId().equals(new Short("195"))) {
			s.setDireccio("Comtes de Bell-lloc, 152");
		} else if (s.getId().equals(new Short("196"))) {
			s.setDireccio("Comtes de Bell-lloc, 189");
		} else if (s.getId().equals(new Short("197"))) {
			s.setDireccio("Taquígraf Serra, 18");
		} else if (s.getId().equals(new Short("198"))) {
			s.setDireccio("Vallespir, 198");
		} else if (s.getId().equals(new Short("199"))) {
			s.setDireccio("Mejía Lequerica, 2");
		} else if (s.getId().equals(new Short("200"))) {
			s.setDireccio("Can Bruixa");
		} else if (s.getId().equals(new Short("201"))) {
			s.setDireccio("Déu i Mata, 56");
		} else if (s.getId().equals(new Short("202"))) {
			s.setDireccio("Les Corts, 20");
		} else if (s.getId().equals(new Short("203"))) {
			s.setDireccio("Av. Diagonal, 617");
		} else if (s.getId().equals(new Short("204"))) {
			s.setDireccio("Av. Diagonal, 672");
		} else if (s.getId().equals(new Short("205"))) {
			s.setDireccio("Av. Diagonal, 629");
		} else if (s.getId().equals(new Short("206"))) {
			s.setDireccio("Av. Diagonal, 650");
		} else if (s.getId().equals(new Short("207"))) {
			s.setDireccio("Av. Diagonal, 640");
		} else if (s.getId().equals(new Short("208"))) {
			s.setDireccio("Av. Diagonal, 632");
		} else if (s.getId().equals(new Short("209"))) {
			s.setDireccio("Diputació, 200");
		} else if (s.getId().equals(new Short("210"))) {
			s.setDireccio("Vilardell, 18");
		} else if (s.getId().equals(new Short("211"))) {
			s.setDireccio("Sancho de Avila, 64");
		} else if (s.getId().equals(new Short("212"))) {
			s.setDireccio("Numància, 212");
		} else if (s.getId().equals(new Short("213"))) {
			s.setDireccio("Dr. Fleming, 9");
		} else if (s.getId().equals(new Short("214"))) {
			s.setDireccio("Dr. Fleming, 19");
		} else if (s.getId().equals(new Short("215"))) {
			s.setDireccio("Francesc Pérez Cabrero");
		} else if (s.getId().equals(new Short("216"))) {
			s.setDireccio("Madrazo, 131");
		} else if (s.getId().equals(new Short("217"))) {
			s.setDireccio("Rector Ubach, 24");
		} else if (s.getId().equals(new Short("218"))) {
			s.setDireccio("Consell de Cent, 556");
		} else if (s.getId().equals(new Short("219"))) {
			s.setDireccio("Laforja, 74");
		} else if (s.getId().equals(new Short("220"))) {
			s.setDireccio("Tuset, 19");
		} else if (s.getId().equals(new Short("221"))) {
			s.setDireccio("Gran de Gràcia (Fontana)");
		} else if (s.getId().equals(new Short("222"))) {
			s.setDireccio("Canó");
		} else if (s.getId().equals(new Short("223"))) {
			s.setDireccio("Bonavista, 14");
		} else if (s.getId().equals(new Short("224"))) {
			s.setDireccio("Girona, 156");
		} else if (s.getId().equals(new Short("225"))) {
			s.setDireccio("Mallorca, 84");
		} else if (s.getId().equals(new Short("226"))) {
			s.setDireccio("Motmany, 1");
		} else if (s.getId().equals(new Short("227"))) {
			s.setDireccio("Torrent de les Flors, 102");
		} else if (s.getId().equals(new Short("228"))) {
			s.setDireccio("Pl. del Nord");
		} else if (s.getId().equals(new Short("229"))) {
			s.setDireccio("Santacreu, 2");
		} else if (s.getId().equals(new Short("230"))) {
			s.setDireccio("Nil Fabra, 30");
		} else if (s.getId().equals(new Short("231"))) {
			s.setDireccio("Secretari Coloma, 59");
		} else if (s.getId().equals(new Short("232"))) {
			s.setDireccio("Vilà i Vilà");
		} else if (s.getId().equals(new Short("233"))) {
			s.setDireccio("Nou de la Rambla, 162");
		} else if (s.getId().equals(new Short("234"))) {
			s.setDireccio("Psg. de l'Exposició, 30");
		} else if (s.getId().equals(new Short("235"))) {
			s.setDireccio("Elkano, 64");
		} else if (s.getId().equals(new Short("236"))) {
			s.setDireccio("França Xica, 42");
		} else if (s.getId().equals(new Short("237"))) {
			s.setDireccio("Rius i Taulet");
		} else if (s.getId().equals(new Short("238"))) {
			s.setDireccio("Espronceda, 298");
		} else if (s.getId().equals(new Short("239"))) {
			s.setDireccio("Indústria, 344");
		} else if (s.getId().equals(new Short("240"))) {
			s.setDireccio("Josep Estivill, 32");
		} else if (s.getId().equals(new Short("241"))) {
			s.setDireccio("Pl. Maragall");
		} else if (s.getId().equals(new Short("242"))) {
			s.setDireccio("Ramon Albo");
		} else if (s.getId().equals(new Short("243"))) {
			s.setDireccio("Alexandre Galí, 7");
		} else if (s.getId().equals(new Short("244"))) {
			s.setDireccio("Felip II");
			// } else if (s.getId().equals(new Short("245"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("246"))) {
			s.setDireccio("Juan de Garay, 116");
		} else if (s.getId().equals(new Short("247"))) {
			s.setDireccio("Olesa, 43");
		} else if (s.getId().equals(new Short("248"))) {
			s.setDireccio("Palència, 31");
		} else if (s.getId().equals(new Short("249"))) {
			s.setDireccio("Vallés i Ribot");
		} else if (s.getId().equals(new Short("250"))) {
			s.setDireccio("Portugal, 12");
		} else if (s.getId().equals(new Short("251"))) {
			s.setDireccio("Cardenal Tedeschini");
		} else if (s.getId().equals(new Short("252"))) {
			s.setDireccio("Pare Manyanet, 23");
		} else if (s.getId().equals(new Short("253"))) {
			s.setDireccio("Onze de Setembre, 37");
		} else if (s.getId().equals(new Short("254"))) {
			s.setDireccio("Gran de St. Andreu, 93");
		} else if (s.getId().equals(new Short("255"))) {
			s.setDireccio("Irlanda, 11");
		} else if (s.getId().equals(new Short("256"))) {
			s.setDireccio("Malats, 17");
		} else if (s.getId().equals(new Short("257"))) {
			s.setDireccio("St. Adrià, 8");
		} else if (s.getId().equals(new Short("258"))) {
			s.setDireccio("Palomar, 12");
		} else if (s.getId().equals(new Short("259"))) {
			s.setDireccio("Bartrina, 14");
		} else if (s.getId().equals(new Short("260"))) {
			s.setDireccio("Pl. de l'Estació");
		} else if (s.getId().equals(new Short("261"))) {
			s.setDireccio("Villarroel, 39");
		} else if (s.getId().equals(new Short("262"))) {
			s.setDireccio("Rocafort, 105");
		} else if (s.getId().equals(new Short("263"))) {
			s.setDireccio("Joan Torras, 28");
		} else if (s.getId().equals(new Short("264"))) {
			s.setDireccio("República Dominicana, 25");
		} else if (s.getId().equals(new Short("265"))) {
			s.setDireccio("Casanovas, 71");
		} else if (s.getId().equals(new Short("266"))) {
			s.setDireccio("Pl. Baró de Viver");
		} else if (s.getId().equals(new Short("267"))) {
			s.setDireccio("Psg. Torras i Bages, 129");
		} else if (s.getId().equals(new Short("268"))) {
			s.setDireccio("Coronel Monasterio, 26");
		} else if (s.getId().equals(new Short("269"))) {
			s.setDireccio("Via Bàrcino, 100");
		} else if (s.getId().equals(new Short("270"))) {
			s.setDireccio("Pl. de la Trinitat");
		} else if (s.getId().equals(new Short("271"))) {
			s.setDireccio("Via Bàrcino, 88");
		} else if (s.getId().equals(new Short("272"))) {
			s.setDireccio("Concepció Arenal, 281");
		} else if (s.getId().equals(new Short("273"))) {
			s.setDireccio("Av. Meridiana, 400");
		} else if (s.getId().equals(new Short("274"))) {
			s.setDireccio("Psg. Fabra i Puig, 94");
		} else if (s.getId().equals(new Short("275"))) {
			s.setDireccio("Rio de Janeiro, 3");
		} else if (s.getId().equals(new Short("276"))) {
			s.setDireccio("Pl. Alfons X");
		} else if (s.getId().equals(new Short("277"))) {
			s.setDireccio("Travessera de Gràcia, 328");
		} else if (s.getId().equals(new Short("278"))) {
			s.setDireccio("Rosalía de Castro, 41");
		} else if (s.getId().equals(new Short("279"))) {
			s.setDireccio("Rda. Guinardó, 145");
		} else if (s.getId().equals(new Short("280"))) {
			s.setDireccio("Indústria, 250");
		} else if (s.getId().equals(new Short("281"))) {
			s.setDireccio("Rda. Guinardó, 170");
		} else if (s.getId().equals(new Short("282"))) {
			s.setDireccio("Font d'en Fargas");
		} else if (s.getId().equals(new Short("283"))) {
			s.setDireccio("Pl. Eivissa");
		} else if (s.getId().equals(new Short("284"))) {
			s.setDireccio("Av. Diagonal, 589");
		} else if (s.getId().equals(new Short("285"))) {
			s.setDireccio("Malats, 98");
		} else if (s.getId().equals(new Short("286"))) {
			s.setDireccio("Bolivia, 76");
		} else if (s.getId().equals(new Short("287"))) {
			s.setDireccio("Gran Via, 630");
		} else if (s.getId().equals(new Short("288"))) {
			s.setDireccio("Pl. Virrei Amat");
		} else if (s.getId().equals(new Short("289"))) {
			s.setDireccio("Múrcia, 58");
		} else if (s.getId().equals(new Short("290"))) {
			s.setDireccio("Pl. dels Jardins d'Alfàbia");
		} else if (s.getId().equals(new Short("291"))) {
			s.setDireccio("Subirats, 5");
		} else if (s.getId().equals(new Short("292"))) {
			s.setDireccio("Amilcar, 1");
		} else if (s.getId().equals(new Short("293"))) {
			s.setDireccio("Petrarca, 44");
		} else if (s.getId().equals(new Short("294"))) {
			s.setDireccio("Seu del Districte (Nou Barris)");
		} else if (s.getId().equals(new Short("295"))) {
			s.setDireccio("St. Iscle, 60");
		} else if (s.getId().equals(new Short("296"))) {
			s.setDireccio("Rosselló i Porcel");
		} else if (s.getId().equals(new Short("297"))) {
			s.setDireccio("Turó Blau");
		} else if (s.getId().equals(new Short("298"))) {
			s.setDireccio("Andreu Nin, 22");
		} else if (s.getId().equals(new Short("299"))) {
			s.setDireccio("Escultor Ordóñez, 55");
		} else if (s.getId().equals(new Short("300"))) {
			s.setDireccio("Alella, 42");
		} else if (s.getId().equals(new Short("301"))) {
			s.setDireccio("Marie Curie, 8");
		} else if (s.getId().equals(new Short("302"))) {
			s.setDireccio("Cavallers, 41");
		} else if (s.getId().equals(new Short("303"))) {
			s.setDireccio("Cavallers, 66");
		} else if (s.getId().equals(new Short("304"))) {
			s.setDireccio("Jimenez Iglesias, 15");
		} else if (s.getId().equals(new Short("305"))) {
			s.setDireccio("Av. Diagonal, 629");
		} else if (s.getId().equals(new Short("306"))) {
			s.setDireccio("Dr. Salvador Cardenal, 1");
		} else if (s.getId().equals(new Short("307"))) {
			s.setDireccio("Pintor Ribalta");
		} else if (s.getId().equals(new Short("308"))) {
			s.setDireccio("Cardenal Reig, 11");
		} else if (s.getId().equals(new Short("309"))) {
			s.setDireccio("St. Ramon Nonat, 26");
		} else if (s.getId().equals(new Short("310"))) {
			s.setDireccio("Josep Samitier, 20");
		} else if (s.getId().equals(new Short("311"))) {
			s.setDireccio("Travessera de les Corts, 58");
		} else if (s.getId().equals(new Short("312"))) {
			s.setDireccio("Felip de Paz, 2");
		} else if (s.getId().equals(new Short("313"))) {
			s.setDireccio("Càceres, 53");
		} else if (s.getId().equals(new Short("314"))) {
			s.setDireccio("Rmbl. del Brasil, 42");
		} else if (s.getId().equals(new Short("315"))) {
			s.setDireccio("Guinardó, 32");
		} else if (s.getId().equals(new Short("316"))) {
			s.setDireccio("Cantàbria, 55");
		} else if (s.getId().equals(new Short("317"))) {
			s.setDireccio("Rbla. Prim, 254");
		} else if (s.getId().equals(new Short("318"))) {
			s.setDireccio("Rda. Guinardó, 83");
		} else if (s.getId().equals(new Short("319"))) {
			s.setDireccio("Amigo, 23");
		} else if (s.getId().equals(new Short("320"))) {
			s.setDireccio("Francolí, 53");
		} else if (s.getId().equals(new Short("321"))) {
			s.setDireccio("Pàdua, 98");
		} else if (s.getId().equals(new Short("322"))) {
			s.setDireccio("Santaló, 165");
		} else if (s.getId().equals(new Short("323"))) {
			s.setDireccio("Vallmajor, 13");
		} else if (s.getId().equals(new Short("324"))) {
			s.setDireccio("Reina Victòria, 31");
		} else if (s.getId().equals(new Short("325"))) {
			s.setDireccio("Alt de Gironella, 13");
		} else if (s.getId().equals(new Short("326"))) {
			s.setDireccio("Castanyer, 16");
		} else if (s.getId().equals(new Short("327"))) {
			s.setDireccio("Reus, 23");
		} else if (s.getId().equals(new Short("328"))) {
			s.setDireccio("Artesa de Segre, 2");
		} else if (s.getId().equals(new Short("329"))) {
			s.setDireccio("Escoles Pies, 99");
		} else if (s.getId().equals(new Short("330"))) {
			s.setDireccio("Dr. Carulla, 44");
		} else if (s.getId().equals(new Short("331"))) {
			s.setDireccio("Castellnou, 58");
		} else if (s.getId().equals(new Short("332"))) {
			s.setDireccio("Dr. Roux, 58");
		} else if (s.getId().equals(new Short("333"))) {
			s.setDireccio("Psg. Senillosa");
		} else if (s.getId().equals(new Short("334"))) {
			s.setDireccio("Via Augusta, 348");
		} else if (s.getId().equals(new Short("335"))) {
			s.setDireccio("Sta. Amèlia, 25");
		} else if (s.getId().equals(new Short("336"))) {
			s.setDireccio("Caponata, 10");
		} else if (s.getId().equals(new Short("337"))) {
			s.setDireccio("Carme Karr, 12");
		} else if (s.getId().equals(new Short("338"))) {
			s.setDireccio("Av. J. Foix, 63");
		} else if (s.getId().equals(new Short("339"))) {
			s.setDireccio("Treball, 264");
		} else if (s.getId().equals(new Short("340"))) {
			s.setDireccio("St. Adrià, 115");
		} else if (s.getId().equals(new Short("341"))) {
			s.setDireccio("Psg. d'Enric Sanchis, 33");
		} else if (s.getId().equals(new Short("342"))) {
			s.setDireccio("Roc Boronat, 134");
		} else if (s.getId().equals(new Short("343"))) {
			s.setDireccio("Campana de la Maquinista");
		} else if (s.getId().equals(new Short("344"))) {
			s.setDireccio("Ciutat d'Asunción, 73");
		} else if (s.getId().equals(new Short("345"))) {
			s.setDireccio("Pl. Teresa de Claramunt");
		} else if (s.getId().equals(new Short("346"))) {
			s.setDireccio("Foneria, 33");
		} else if (s.getId().equals(new Short("347"))) {
			s.setDireccio("Mare de Déu dels Ports, 235");
		} else if (s.getId().equals(new Short("348"))) {
			s.setDireccio("Jardins de Can Ferrero");
		} else if (s.getId().equals(new Short("349"))) {
			s.setDireccio("Energia, 2");
		} else if (s.getId().equals(new Short("350"))) {
			s.setDireccio("Villarroel, 208");
		} else if (s.getId().equals(new Short("351"))) {
			s.setDireccio("Jane Adams, 26");
		} else if (s.getId().equals(new Short("352"))) {
			s.setDireccio("Radi, 10");
		} else if (s.getId().equals(new Short("353"))) {
			s.setDireccio("Munne, 2");
		} else if (s.getId().equals(new Short("354"))) {
			s.setDireccio("Rmbl. del Brasil, 1");
		} else if (s.getId().equals(new Short("355"))) {
			s.setDireccio("Canalejas, 87");
		} else if (s.getId().equals(new Short("356"))) {
			s.setDireccio("Balcells, 48");
		} else if (s.getId().equals(new Short("357"))) {
			s.setDireccio("Cardener, 87");
		} else if (s.getId().equals(new Short("358"))) {
			s.setDireccio("Gombau, 24");
		} else if (s.getId().equals(new Short("359"))) {
			s.setDireccio("Méndez Nuñez, 16");
		} else if (s.getId().equals(new Short("360"))) {
			s.setDireccio("Bailén, 62");
		} else if (s.getId().equals(new Short("361"))) {
			s.setDireccio("Psg. de Colom (Les Rambles)");
		} else if (s.getId().equals(new Short("362"))) {
			s.setDireccio("Bailén, 98");
		} else if (s.getId().equals(new Short("363"))) {
			s.setDireccio("Bruc, 20");
		} else if (s.getId().equals(new Short("364"))) {
			s.setDireccio("Psg. de Gràcia, 73");
		} else if (s.getId().equals(new Short("365"))) {
			s.setDireccio("Viladomat, 244");
		} else if (s.getId().equals(new Short("366"))) {
			s.setDireccio("Loreto, 49");
		} else if (s.getId().equals(new Short("367"))) {
			s.setDireccio("Nicaragua, 96");
		} else if (s.getId().equals(new Short("368"))) {
			s.setDireccio("Diputació, 350");
		} else if (s.getId().equals(new Short("369"))) {
			s.setDireccio("Consell de Cent, 509");
		} else if (s.getId().equals(new Short("370"))) {
			s.setDireccio("Sardenya, 324");
		} else if (s.getId().equals(new Short("371"))) {
			s.setDireccio("Enamorats, 49");
		} else if (s.getId().equals(new Short("372"))) {
			s.setDireccio("Padilla, 159");
		} else if (s.getId().equals(new Short("373"))) {
			s.setDireccio("Paral·lel, 130");
		} else if (s.getId().equals(new Short("374"))) {
			s.setDireccio("Psg. de Gràcia, 89");
		} else if (s.getId().equals(new Short("375"))) {
			s.setDireccio("World Trade Center");
		} else if (s.getId().equals(new Short("376"))) {
			s.setDireccio("World Trade Center");
		} else if (s.getId().equals(new Short("377"))) {
			s.setDireccio("Pl. Ictíneo");
		} else if (s.getId().equals(new Short("378"))) {
			s.setDireccio("Pl. Joaquim Xira i Palau");
		} else if (s.getId().equals(new Short("379"))) {
			s.setDireccio("Pl. Sant Miguel");
		} else if (s.getId().equals(new Short("380"))) {
			s.setDireccio("Duran i Bas, 2");
		} else if (s.getId().equals(new Short("381"))) {
			s.setDireccio("Agustí Duran i Sempere, 10");
		} else if (s.getId().equals(new Short("382"))) {
			s.setDireccio("Lope de Vega, 79");
			// } else if (s.getId().equals(new Short("383"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("384"))) {
			s.setDireccio("Vilamarí, 85");
		} else if (s.getId().equals(new Short("385"))) {
			s.setDireccio("Casanova, 119");
		} else if (s.getId().equals(new Short("386"))) {
			s.setDireccio("Paral·lel, 162");
		} else if (s.getId().equals(new Short("387"))) {
			s.setDireccio("Nàpols, 125");
		} else if (s.getId().equals(new Short("388"))) {
			s.setDireccio("Riera Alta, 6");
		} else if (s.getId().equals(new Short("389"))) {
			s.setDireccio("Parc de la Ciutadella");
		} else if (s.getId().equals(new Short("390"))) {
			s.setDireccio("Comerç, 36");
		} else if (s.getId().equals(new Short("391"))) {
			s.setDireccio("Jaume Brossa, 49");
		} else if (s.getId().equals(new Short("392"))) {
			s.setDireccio("Ramon Turró, 3");
		} else if (s.getId().equals(new Short("393"))) {
			s.setDireccio("Llacuna, 86");
		} else if (s.getId().equals(new Short("394"))) {
			s.setDireccio("Diputació, 226");
		} else if (s.getId().equals(new Short("395"))) {
			s.setDireccio("Pl. Catalunya (La Rambla)");
		} else if (s.getId().equals(new Short("396"))) {
			s.setDireccio("Joan Miró, 2");
		} else if (s.getId().equals(new Short("397"))) {
			s.setDireccio("Av. Litoral, 24");
		} else if (s.getId().equals(new Short("398"))) {
			s.setDireccio("Psg. Marítim (Barceloneta)");
			// } else if (s.getId().equals(new Short("399"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("400"))) {
			s.setDireccio("Moll Nou, 92");
		} else if (s.getId().equals(new Short("401"))) {
			s.setDireccio("Psg. de Colom");
		} else if (s.getId().equals(new Short("402"))) {
			s.setDireccio("Psg. de Colom");
			// } else if (s.getId().equals(new Short("403"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("404"))) {
			s.setDireccio("Juan Gris, 28");
		} else if (s.getId().equals(new Short("405"))) {
			s.setDireccio("Pl. Comercial");
		} else if (s.getId().equals(new Short("406"))) {
			s.setDireccio("Gran Via, 594");
		} else if (s.getId().equals(new Short("407"))) {
			s.setDireccio("Ramon Trias Fargas, 21");
		} else if (s.getId().equals(new Short("408"))) {
			s.setDireccio("Villena, 11");
		} else if (s.getId().equals(new Short("409"))) {
			s.setDireccio("Joan d'Àustria, 50");
		} else if (s.getId().equals(new Short("410"))) {
			s.setDireccio("Psg. de Colom");
		} else if (s.getId().equals(new Short("411"))) {
			s.setDireccio("Rmbl. Catalunya, 31");
		} else if (s.getId().equals(new Short("412"))) {
			s.setDireccio("Pl. Urquinaona, 3");
		} else if (s.getId().equals(new Short("413"))) {
			s.setDireccio("Bruc, 68");
		} else if (s.getId().equals(new Short("414"))) {
			s.setDireccio("Casp, 67");
		} else if (s.getId().equals(new Short("415"))) {
			s.setDireccio("Rbla. Raval, 13");
		} else if (s.getId().equals(new Short("416"))) {
			s.setDireccio("Rbla. Raval, 20");
			// } else if (s.getId().equals(new Short("417"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("418"))) {
			s.setDireccio("Pg. de Lluís Companys");
		} else if (s.getId().equals(new Short("419"))) {
			s.setDireccio("Pg. de Lluís Companys");
		} else if (s.getId().equals(new Short("420"))) {
			s.setDireccio("Gran Via, 353");
		} else if (s.getId().equals(new Short("421"))) {
			s.setDireccio("Pl. Joan Peiró");
			// } else if (s.getId().equals(new Short("422"))){
			// s.setDireccio("XXXXXX");
		} else if (s.getId().equals(new Short("423"))) {
			s.setDireccio("Hondures, 32");
		} else if (s.getId().equals(new Short("424"))) {
			s.setDireccio("Psg. Marítim (Barceloneta)");
		} else if (s.getId().equals(new Short("425"))) {
			s.setDireccio("Cervelló, 5");
		} else if (s.getId().equals(new Short("426"))) {
			s.setDireccio("Ribes, 59");
		} else if (s.getId().equals(new Short("427"))) {
			s.setDireccio("St. Pau, 119");
		} else if (s.getId().equals(new Short("428"))) {
			s.setDireccio("Pujades, 103");
			// } else if (s.getId().equals(new Short("429"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("430"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("431"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("432"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("433"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("434"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("435"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("436"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("437"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("438"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("439"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("440"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("441"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("442"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("443"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("444"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("445"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("446"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("447"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("448"))){
			// s.setDireccio("XXXXXX");
			// } else if (s.getId().equals(new Short("449"))){
			// s.setDireccio("XXXXXX");

		}
	}
}