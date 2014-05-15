package cat.pipo.bicing.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**Aquesta classe és com la Station però sense el String i les
 * coordenades. Osease, la idea és que ocupi una merda.
 *
 * @author Roc Boronat
 * @date 16/04/2010
 */
public class StationTiny implements Externalizable{
	private static final long serialVersionUID = -3592096330235395832L;

	byte foratsPlens;
	byte foratsBuits;

	public StationTiny (){
	}
	public StationTiny (String d, byte fp, byte fb, double x, double y){
		setForatsPlens(fp);
		setForatsBuits(fb);
	}
	public StationTiny (Station t){
		setForatsPlens(t.getForatsPlens());
		setForatsBuits(t.getForatsBuits());
	}

	public byte getForatsBuits() {
		return foratsBuits;
	}
	public void setForatsBuits(byte foratsBuits) {
		this.foratsBuits = foratsBuits;
	}
	public byte getForatsPlens() {
		return foratsPlens;
	}
	public void setForatsPlens(byte foratsPlens) {
		this.foratsPlens = foratsPlens;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bicicletes: " + getForatsPlens() +"\n");
		sb.append("Espais lliures: " + getForatsBuits());
		return sb.toString();
	}

    public void readExternal(ObjectInput in)
    throws IOException,ClassNotFoundException {
    	foratsPlens = in.readByte();
    	foratsBuits = in.readByte();
    }

    public void writeExternal(ObjectOutput out)
    throws IOException {
    	out.writeByte(foratsPlens);
    	out.writeByte(foratsBuits);
    }
}
