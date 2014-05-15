package cat.pipo.bicing.cities.spain.santvicens;

import java.util.Date;

import cat.pipo.bicing.Globals;

public class SantVicensThread extends Thread{

	@Override
	public void run() {
		super.run();

		while (Globals.THREADS_RUNNING){
			try {
				if (SantVicensDAO.loadStations())
					setLastUpdate(new Date());

//				Globals.updateKML();

				sleep(60000); //Cada 10 segons

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Date getLastUpdate() {
		return SantVicensGlobals.LAST_UPDATE;
	}
	public void setLastUpdate(Date lu) {
		SantVicensGlobals.LAST_UPDATE = lu;
	}
}
