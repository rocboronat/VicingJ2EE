package cat.pipo.errorLogger.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import cat.pipo.errorLogger.PipoLog;
import cat.pipo.errorLogger.config.Globals;
import cat.pipo.errorLogger.util.Chrono;

public class SimpleLogSender {

	public static void send(PipoLog p) {
		Chrono c = new Chrono();
		c.start(1);

		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Globals.URL);

			//Setejem el timeout de la acció a 10 segons.
			post.getParams().setParameter("http.socket.timeout", new Integer(10000));


	        ByteArrayOutputStream bos = serialize(p);
	        HttpEntity e = new ByteArrayEntity(bos.toByteArray());
	        post.setEntity(e);
	        client.execute(post);

	        //HttpResponse res = client.execute(post);

//	        InputStream is =res.getEntity().getContent();
//	        byte b[] = new byte[1000];
//	        int llegit = is.read(b);
//	        System.out.println(new String(b,0,llegit));

		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println("S'han trigat " + c.stop(1) + "ms a enviar la petició HTTP.");
	}

	/** Cosa que envia síncronament les peticions HTTP.
	 * Osease, si executes el send(), no sortiras d'aquestes línies
	 * fins que la petició hagi anat, i tornat.
	 *
	 * @author: Roc Boronat
	 * @date 10/03/2009
	 */
	public static void send(Exception exception, Long time, String logId) {
		PipoLog p = new PipoLog(exception,time,Globals.APPID,logId);
		send(p);
	}

	private static ByteArrayOutputStream serialize(Object o) throws IOException{
        ByteArrayOutputStream bs= new ByteArrayOutputStream();
        ObjectOutputStream os;
        os = new ObjectOutputStream (bs);
        os.writeObject(o);
        os.close();
        return bs;
    }
}
