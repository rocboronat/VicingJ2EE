package cat.pipo.bicing.cities.spain.santvicens;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cat.pipo.bicing.bean.Station;
import cat.pipo.bicing.bean.StationTiny;

public class SantVicensGlobals {
	public static Boolean SEMAFOR = false;
	public static ArrayList STATIONS = new ArrayList<Station>(); //Inicialitzem amb un HashMap buit.
	public static HashMap STATIONS_TINY = new HashMap<Short,StationTiny>(); //Inicialitzem amb un HashMap buit.
	public static Date LAST_UPDATE = new Date(); //Inicialitzem amb un now(), de guays.
}
