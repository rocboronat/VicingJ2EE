package cat.pipo.bicing;

import java.util.Iterator;
import java.util.List;


public class Globals {

	public static Boolean SEMAFOR = true;

	public static boolean THREADS_RUNNING = true;
	public static String KML = new String(); //Inicialitzem amb  un KML buit.

	public static void eraseList(List l){
//		Chrono c = new Chrono();
//		c.start(1);
		for (Iterator iter = l.iterator(); iter.hasNext();) {
			Object o = iter.next();
			o = null;
		}
		l = null;
//		System.out.println("Erasing a list token " + c.stop(1) +"ms");
	}

//	public static void updateKML(){
//
//		List<Station> llistaStations = new ArrayList<Station>();
//		llistaStations.addAll(GironaGlobals.STATIONS);
//		llistaStations.addAll(BarcelonaGlobals.STATIONS);
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		sb.append("<kml xmlns=\"http://earth.google.es/kml/2.0\">");
//
//		sb.append("<Document><name>KML Piposerver</name><description>Google Earth markers</description>");
//		sb.append("<Style id=\"icono\"><IconStyle><Icon><href>http://bicing.cat/pfw_files/tpl/icono_mark_2.png</href></Icon></IconStyle></Style>");
//		sb.append("<Style id=\"icono_blue\"><IconStyle><Icon><href>http://bicing.cat/pfw_files/tpl/icono_mark_blue_2.png</href></Icon></IconStyle></Style>");
//		sb.append("<Style id=\"icono_green\"><IconStyle><Icon><href>http://bicing.cat/pfw_files/tpl/icono_mark_green_2.png</href></Icon></IconStyle></Style>");
//
//		for (Station s:llistaStations){
//			sb.append("<Placemark>");
//
//			sb.append("<description>");
//			sb.append("<![CDATA[");
//			//sb.append(s.getDireccio()); //EIII TEXT!)
//			sb.append(s.getId()+". <b>".concat(s.getDireccio()).concat("</b>"));
//			sb.append("<br/>Bicings: <b>" + s.getForatsPlens() + "</b>");
//			sb.append("<br/>Free slots: <b>" + s.getForatsBuits() + "</b>");
//			sb.append("]]>");
//			sb.append("</description>");
//
//			sb.append("<styleUrl>");
//			if (s.getForatsBuits()==0)
//				sb.append("#icono_blue");
//			else if (s.getForatsPlens()==0)
//				sb.append("#icono");
//			else
//				sb.append("#icono_green");
//			sb.append("</styleUrl>");
//
//			sb.append("<Point>");
//			sb.append("<coordinates>");
//			sb.append(s.getCoords());
//			sb.append(",13</coordinates>");
//			sb.append("</Point>");
//
//			sb.append("</Placemark>");
//		}
//
//		sb.append("</Document></kml>");
//
//		String kmlFinal = sb.toString();
//		kmlFinal = kmlFinal.replaceAll("'", "\\\\'");
//		KML = sb.toString();
//	}
}
