package cat.pipo.errorLogger.api;

import cat.pipo.errorLogger.PipoLog;
import cat.pipo.errorLogger.util.LogThread;

public class SimpleLogThread {

	/** Cosa que envia ASSíncronament les peticions HTTP.
	 * Osease, si executes el send(), s'executarà en un fly
	 * i no hauras d'esperar a que la petició HTTP vagi i torni.
	 *
	 * @author: Roc Boronat
	 * @date 10/03/2009
	 */
	public static void send(Exception e, Long t, String l){
		LogThread lt = new LogThread(e, t, l);
		lt.start();
	}

	/** Cosa que envia ASSíncronament les peticions HTTP.
	 * Osease, si executes el send(), s'executarà en un fly
	 * i no hauras d'esperar a que la petició HTTP vagi i torni.
	 *
	 * @author: Roc Boronat
	 * @date 10/03/2009
	 */
	public static void send(PipoLog p){
		LogThread lt = new LogThread(p);
		lt.start();
	}
}
