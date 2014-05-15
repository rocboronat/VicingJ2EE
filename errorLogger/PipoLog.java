package cat.pipo.errorLogger;

import java.io.Serializable;
import java.util.Map;

/** Classe serialitzable que representa tot el que es vol guardar
 * d'una acció en concret.
 *
 * @author Roc Boronat
 * @date 10/03/2009
 */
public class PipoLog implements Serializable{

	private static final long serialVersionUID = -3191077325481402129L;

	Exception exception = null; //La Exe
	Long time = null; //El temps que ha trigat executar tal acció. -1 = npi
	String appId = null; //Per exemple, "fancyBicing"
	String logId = null; //Per exemple, "Iniciar Aplicacio"
	Map map = null; //Pq el programador pugui posar coses a saco.


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("appId: " + this.appId + "\n");
		sb.append("logId: " + this.logId + "\n");
		if (this.time!=-1)
			sb.append("time: " + this.time + "\n");
		if (this.exception!=null)
			sb.append("exceptionMessage: " + exception.getMessage() + "\n");
		return sb.toString();
	}

	public PipoLog(Exception e, Long t, String app, String log) {
		this.exception = e;
		this.time = t;
		this.appId = app;
		this.logId = log;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception e) {
		this.exception = e;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
