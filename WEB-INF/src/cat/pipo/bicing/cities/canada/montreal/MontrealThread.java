package cat.pipo.bicing.cities.canada.montreal;

import java.util.Date;

import cat.pipo.bicing.Globals;

public class MontrealThread extends Thread{

	@Override
	public void run() {
		super.run();

		while (Globals.THREADS_RUNNING){
			try {
				if (MontrealDAO.loadStations())
					setLastUpdate(new Date());

//				Globals.updateKML();

				sleep(30000); //Cada 30 segons

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Date getLastUpdate() {
		return MontrealGlobals.LAST_UPDATE;
	}
	public void setLastUpdate(Date lu) {
		MontrealGlobals.LAST_UPDATE = lu;
	}
}
