package cat.pipo.bicing.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.StringTokenizer;

/**
 * Classe per a representar una Estació.
 * 
 * @author Roc Boronat
 * @date 16/04/2010
 */
public class Station implements Externalizable, Comparable {

	public static final short KEY_BARCELONA = 1;
	public static final short KEY_GIRONA = 2;
	public static final short KEY_GRANOLLERS = 3;
	public static final short KEY_MONTREAL = 4;
	public static final short KEY_SANTVICENS = 5;

	private short serviceKey = 0;

	public short getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(short serviceKey) {
		this.serviceKey = serviceKey;
	}

	private static final long serialVersionUID = -3592096330235395832L;

	Short id;
	String direccio;
	byte foratsPlens;
	byte foratsBuits;

	double x;
	double y;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getCoords() {
		return (getY() + "," + getX());
	}

	public Station() {
	}

	public Station(short i, String d, byte fp, byte fb, double x, double y) {
		setId(i);
		setDireccio(d);
		setForatsPlens(fp);
		setForatsBuits(fb);
		setX(x);
		setY(y);
	}

	public String getDireccio() {
		return direccio;
	}

	public void setDireccio(String direccio) {
		direccio = direccio.replaceAll("\\\\", ""); // per solucionar un typical
													// problem de parsing.
		this.direccio = direccio.trim();
	}

	public Byte getForatsBuits() {
		return foratsBuits;
	}

	public void setForatsBuits(byte foratsBuits) {
		this.foratsBuits = foratsBuits;
	}

	public Byte getForatsPlens() {
		return foratsPlens;
	}

	public void setForatsPlens(byte foratsPlens) {
		this.foratsPlens = foratsPlens;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public byte getForatsTotals() {
		return (byte) (getForatsBuits() + getForatsPlens());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + getId() + "\n");
		sb.append("Direcció: " + getDireccio() + "\n");
		sb.append("Bicicletes: " + getForatsPlens() + "\n");
		sb.append("Espais lliures: " + getForatsBuits() + "\n");
		sb.append("Coords: '" + y + "," + x + "'");
		return sb.toString();
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		id = in.readShort();
		setDireccio(in.readUTF());
		foratsPlens = in.readByte();
		foratsBuits = in.readByte();
		x = in.readDouble();
		y = in.readDouble();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeShort(id);
		out.writeUTF(direccio);
		out.writeByte(foratsPlens);
		out.writeByte(foratsBuits);
		out.writeDouble(x);
		out.writeDouble(y);
	}

	public int compareTo(Object o) {
		Station altre = (Station) o;

		if (serviceKey == KEY_BARCELONA) { // per a ordenar Barcelona
			StringTokenizer s1 = new StringTokenizer(this.getDireccio(), ",");
			StringTokenizer s2 = new StringTokenizer(altre.getDireccio(), ",");

			String t1;
			if (s1.hasMoreTokens()) {
				t1 = clean(s1.nextToken());
			} else {
				t1 = "";
			}
			String t2;
			if (s2.hasMoreTokens()) {
				t2 = clean(s2.nextToken());
			} else {
				t2 = "";
			}

			if (t1.equals(""))
				return 1;
			else if (t2.equals(""))
				return -1;

			if (!t1.equals(t2)) { // si no són estacions al mateix carrer...
				if (t1.equals(t2)) // Si mateixa direcció,
					return this.getId().compareTo(altre.getId()); // Comparem
																	// ID.
				return t1.compareTo(t2);
			} else { // si són estacions del mateix carrer...
				// carreguem el nombre a la t1 i t2
				if (!s1.hasMoreTokens() || !s2.hasMoreTokens()) {
					return 0; // iguals
				}
				t1 = s1.nextToken();
				t2 = s2.nextToken();
				Integer d1 = new Integer(t1.trim());
				Integer d2 = new Integer(t2.trim());
				return d1.compareTo(d2);
			}
		} else { // per a ordenar la resta de ciutats
			if (this.getDireccio().equals(altre.getDireccio())) // Si mateixa
																// direcció,
				return this.getId().compareTo(altre.getId()); // Comparem ID.
			return this.getDireccio().compareTo(altre.getDireccio());
		}
	}

	private static String clean(String q) {
		String query = q.toLowerCase().replace('á', 'a').replace('à', 'a')
				.replace('è', 'e').replace('é', 'e').replace('í', 'i')
				.replace('ì', 'i').replace('ó', 'o').replace('ò', 'o')
				.replace('ú', 'u').replace('ù', 'u').replace('ç', 'c')
				.replace('ñ', 'n').replace('-', ' ').replace(',', ' ')
				.replace('\'', ' ').replace('·', ' ').replace('/', ' ')
				.replace('(', ' ').replace(')', ' ').replaceAll(" ", "");

		return query;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Station) {
			return this.id.equals(((Station) obj).id);
		} else {
			return false;
		}
	}

}
