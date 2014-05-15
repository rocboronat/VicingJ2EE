package cat.pipo.bicing.cities.spain.barcelona.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cat.pipo.bicing.bean.Station;

public class StationNavigationSaxHandler extends DefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private static final String STATION = "station";
	private static final String ID = "id";
	private static final String LATITUDE = "lat";
	private static final String LONGITUDE = "long";
	private static final String STREET = "street";
	private static final String STREETNUMBER = "streetNumber";
	private static final String STATUS = "status";
	private static final String SLOTS = "slots";
	private static final String BIKES = "bikes";
	private boolean inIdTag = false;
	private boolean inLatitudeTag = false;
	private boolean inLongitudeTag = false;
	private boolean inStreetTag = false;
	private boolean inStreetNumberTag = false;
	private boolean inSlotsTag = false;
	private boolean inBikesTag = false;
	private boolean inStatusTag = false;

	private StationNavigationDataSet navigationDataSet = new StationNavigationDataSet();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public StationNavigationDataSet getParsedData() {
		return this.navigationDataSet;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.navigationDataSet = new StationNavigationDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String qName, String localName, Attributes atts) throws SAXException {
		// if (localName.equals("kml")) {
		// this.in_kmltag = true;
		if (localName.equals(STATION)) {
			navigationDataSet.currentStation = new Station();
		} else if (localName.equals(ID)) {
			this.inIdTag = true;
		} else if (localName.equals(LATITUDE)) {
			this.inLatitudeTag = true;
		} else if (localName.equals(LONGITUDE)) {
			this.inLongitudeTag = true;
		} else if (localName.equals(STREET)) {
			this.inStreetTag = true;
		} else if (localName.equals(STREETNUMBER)) {
			this.inStreetNumberTag = true;
		} else if (localName.equals(STATUS)) {
			this.inStatusTag = true;
		} else if (localName.equals(SLOTS)) {
			this.inSlotsTag = true;
		} else if (localName.equals(BIKES)) {
			this.inBikesTag = true;
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String qName, String localName) throws SAXException {
		if (localName.equals(STATION)) {
			navigationDataSet.addCurrentStation();
		} else if (localName.equals(ID)) {
			this.inIdTag = false;
		} else if (localName.equals(LATITUDE)) {
			this.inLatitudeTag = false;
		} else if (localName.equals(LONGITUDE)) {
			this.inLongitudeTag = false;
		} else if (localName.equals(STREET)) {
			this.inStreetTag = false;
		} else if (localName.equals(STREETNUMBER)) {
			this.inStreetNumberTag = false;
		} else if (localName.equals(STATUS)) {
			this.inStatusTag = false;
		} else if (localName.equals(SLOTS)) {
			this.inSlotsTag = false;
		} else if (localName.equals(BIKES)) {
			this.inBikesTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		String s = new String(ch, start, length);
		try {
			if (inIdTag) {
				navigationDataSet.currentStation.setId(Short.valueOf(s.trim()));
			} else if (inLatitudeTag) {
				navigationDataSet.currentStation.setY(Double.valueOf(s));
			} else if (inLongitudeTag) {
				navigationDataSet.currentStation.setX(Double.valueOf(s));
			} else if (inStreetTag) {
				navigationDataSet.currentStation.setDireccio(s);
			} else if (inStreetNumberTag) {
				navigationDataSet.currentStation.setDireccio(navigationDataSet.currentStation.getDireccio().concat(" " + s));
				// } else if (inStatusTag) {
				// navigationDataSet.currentStation.setStatus(new String(ch, start, length));
			} else if (inSlotsTag) {
				navigationDataSet.currentStation.setForatsBuits(Byte.valueOf(s));
			} else if (inBikesTag) {
				navigationDataSet.currentStation.setForatsPlens(Byte.valueOf(s));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
