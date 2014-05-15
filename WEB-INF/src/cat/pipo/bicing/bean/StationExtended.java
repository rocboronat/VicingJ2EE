package cat.pipo.bicing.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**Classe per a representar una Estació.
 *
 * @author Roc Boronat
 * @date 27/12/2010
 */
public class StationExtended extends Station{

	private static final long serialVersionUID = -2161572699700703172L;

	Integer id;

	@Deprecated
	@Override
	public Short getId() {
		return null;
	}
	public Integer getIdExtended() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public StationExtended (){
	}
	public StationExtended (Integer i, String d, byte fp, byte fb, double x, double y){
		setId(i);
		setDireccio(d);
		setForatsPlens(fp);
		setForatsBuits(fb);
		setX(x);
		setY(y);
	}

    public void readExternal(ObjectInput in)
    throws IOException,ClassNotFoundException {
    	id = in.readInt();
    	setDireccio(in.readUTF());
    	foratsPlens = in.readByte();
    	foratsBuits = in.readByte();
    	x = in.readDouble();
    	y = in.readDouble();
    }

    public void writeExternal(ObjectOutput out)
    throws IOException {
    	out.writeInt(id);
    	out.writeUTF(direccio);
    	out.writeByte(foratsPlens);
    	out.writeByte(foratsBuits);
    	out.writeDouble(x);
    	out.writeDouble(y);
    }
}
