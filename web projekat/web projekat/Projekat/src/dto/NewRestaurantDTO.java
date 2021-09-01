package dto;

public class NewRestaurantDTO {

	public String name;
	public String type;
	public String status;
	public String longitude;
	public String latitude;
	public String adress;
	public String logo;
	public String managerUsername;

	@Override
	public String toString() {
		return "NewRestaurantDTO [name=" + name + ", type=" + type + ", status=" + status + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", adress=" + adress + ", logo=" + logo + ", managerUsername="
				+ managerUsername + "]";
	}
}
