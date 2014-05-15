package cat.pipo.errorLogger;

import java.util.HashMap;
import java.util.Map;

import cat.pipo.errorLogger.api.SimpleLogThread;
import cat.pipo.errorLogger.util.Chrono;



public class Main {
	public static void main(String[] args) {
		Chrono c = new Chrono();
		try {
			c.start(1);
			Integer i = 0;
			while (i<200000){
				i++;
			}
			//System.out.println("S'han trigat " + c.checkpoint(1) + "ms a processar el while");
			double a = 50 / 0;
		} catch (Exception e) {
			//SimpleLogThread.send(e, c.stop(1), "prova");

			PipoLog p = new PipoLog(e, 0L, "App Prova", "Log Prova");
			Map m = new HashMap();
			m.put("1", "a");
			m.put("2", "b");
			m.put("3", "c");
			p.setMap(m);
			SimpleLogThread.send(p);
		}
	}
}
