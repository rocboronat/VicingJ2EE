package cat.pipo.bicing.cities.spain.granollers;

import java.util.Date;

import cat.pipo.bicing.Globals;

public class GranollersThread extends Thread{

	@Override
	public void run() {
		super.run();
/*
		while (Globals.THREADS_RUNNING){
			try {
				if (GranollersDAO.loadStations())
					setLastUpdate(new Date());

//				Globals.updateKML();

				sleep(60000); //Cada 10 segons

			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
	}

	public Date getLastUpdate() {
		return GranollersGlobals.LAST_UPDATE;
	}
	public void setLastUpdate(Date lu) {
		GranollersGlobals.LAST_UPDATE = lu;
	}
}
