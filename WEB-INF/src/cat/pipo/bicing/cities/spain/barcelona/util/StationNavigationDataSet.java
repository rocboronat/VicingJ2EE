package cat.pipo.bicing.cities.spain.barcelona.util;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import cat.pipo.bicing.bean.Station;

public class StationNavigationDataSet {

	private ArrayList<Station> stations = new ArrayList<Station>();
	public Station currentStation;

	public void addCurrentStation() {
		stations.add(currentStation);
	}

	public ArrayList<Station> getStations() {
		return stations;
	}

	public static StationNavigationDataSet getStationsNavigationDataSet(String xml) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			StationNavigationSaxHandler navSaxHandler = new StationNavigationSaxHandler();
			xmlreader.setContentHandler(navSaxHandler);
			InputSource is = new InputSource(new StringReader(xml));
			xmlreader.parse(is);
			return navSaxHandler.getParsedData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
