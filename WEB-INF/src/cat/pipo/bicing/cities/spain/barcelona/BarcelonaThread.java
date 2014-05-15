package cat.pipo.bicing.cities.spain.barcelona;

import java.util.Date;

import cat.pipo.bicing.Globals;

public class BarcelonaThread extends Thread {

	@Override
	public void run() {
		super.run();

		while (Globals.THREADS_RUNNING) {
			try {
				if (BarcelonaDAO.loadStations())
					setLastUpdate(new Date());

				// Globals.updateKML();

				sleep(10000); // Cada 10 segons

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Date getLastUpdate() {
		return BarcelonaGlobals.LAST_UPDATE;
	}

	public void setLastUpdate(Date lu) {
		BarcelonaGlobals.LAST_UPDATE = lu;
	}
}
