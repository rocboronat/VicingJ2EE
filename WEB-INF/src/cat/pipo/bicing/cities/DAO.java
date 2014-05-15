package cat.pipo.bicing.cities;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationExtended;
import cat.pipo.bicing.bean.StationTiny;

public class DAO {
	protected static Station loadStation(List llistaStations, short id) {
		for (Iterator iter = llistaStations.iterator(); iter.hasNext();) {
			Station s = (Station) iter.next();
			if (s.getId().equals(id))
				return s;
		}
		return null;
	}

	protected static StationExtended loadStation(List llistaStations, Integer id) {
		for (Iterator iter = llistaStations.iterator(); iter.hasNext();) {
			StationExtended s = (StationExtended) iter.next();
			if (s.getIdExtended().equals(id))
				return s;
		}
		return null;
	}


	protected static StationTiny loadStationTiny(Map mapStationsTiny, Short id) {
		for (Iterator iter = mapStationsTiny.keySet().iterator(); iter.hasNext();) {
			Short stId = (Short) iter.next();
			if (stId.equals(id))
				return (StationTiny)mapStationsTiny.get(stId);
		}
		return null;
	}

	protected static StationTiny loadStationTiny(Map mapStationsTiny, Integer id) {
		for (Iterator iter = mapStationsTiny.keySet().iterator(); iter.hasNext();) {
			Integer stId = (Integer) iter.next();
			if (stId.equals(id))
				return (StationTiny)mapStationsTiny.get(stId);
		}
		return null;
	}
}
