package cat.pipo.errorLogger.util;

import cat.pipo.errorLogger.PipoLog;
import cat.pipo.errorLogger.api.SimpleLogSender;

public class LogThread extends Thread{

	Exception exception;
	Long time;
	String logId;
	PipoLog pipolog;

	public LogThread(Exception e, Long t, String l) {
		exception = e;
		time = t;
		logId = l;
	}

	public LogThread(PipoLog p) {
		pipolog = p;
	}

	@Override
	public void run() {
		super.run();

		try {
			this.sleep(2000); //Ens esperem un parell de segons...
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (pipolog==null)
			SimpleLogSender.send(exception, time, logId);
		else
			SimpleLogSender.send(pipolog);
	}
}
