package beans.entities;

import java.io.Serializable;

public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double longitude;
	private double latitude;
	private String adress; // form:street number,place/town,post number

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public Location() {
	}

	public Location(double longitude, double latitude, String adress) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.adress = adress;
	}

}
