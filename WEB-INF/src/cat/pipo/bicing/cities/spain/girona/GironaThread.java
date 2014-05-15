package cat.pipo.bicing.cities.spain.girona;

import java.util.Date;

import cat.pipo.bicing.Globals;

public class GironaThread extends Thread{

	@Override
	public void run() {
		super.run();

		while (Globals.THREADS_RUNNING){
			try {
				if (GironaDAO.loadStations())
					setLastUpdate(new Date());

//				Globals.updateKML();

				sleep(30000); //Cada 10 segons

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Date getLastUpdate() {
		return GironaGlobals.LAST_UPDATE;
	}
	public void setLastUpdate(Date lu) {
		GironaGlobals.LAST_UPDATE = lu;
	}
}
