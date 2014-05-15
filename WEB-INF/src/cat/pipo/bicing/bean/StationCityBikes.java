package cat.pipo.bicing.bean;

public class StationCityBikes {

	int id;
	String name;
	String lat;
	String lng;
	String timestamp;
	String bikes;
	String free;

	public String getBikes() {
		return bikes;
	}

	public void setBikes(String bikes) {
		this.bikes = bikes;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public StationCityBikes(){
	}
}
