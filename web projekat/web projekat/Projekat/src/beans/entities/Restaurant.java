package beans.entities;

import java.io.Serializable;
import java.util.ArrayList;

import beans.enums.RestaurantStatus;
import beans.enums.RestaurantType;

public class Restaurant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private RestaurantType type;
	private ArrayList<Article> articles;
	private RestaurantStatus status;
	private Location location;
	private String logo;
	private double averageGrade;
	private String managerUsername;
	private boolean deleted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RestaurantType getType() {
		return type;
	}

	public void setType(RestaurantType type) {
		this.type = type;
	}

	public ArrayList<Article> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<Article> articles) {
		this.articles = articles;
	}

	public RestaurantStatus getStatus() {
		return status;
	}

	public void setStatus(RestaurantStatus status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public String getManagerUsername() {
		return managerUsername;
	}

	public void setManagerUsername(String managerUsername) {
		this.managerUsername = managerUsername;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Restaurant() {
	}

	public Restaurant(String id, String name, RestaurantType type, ArrayList<Article> articles, RestaurantStatus status,
			Location location, String logo, double averageGrade, String managerUsername, boolean deleted) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.articles = articles;
		this.status = status;
		this.location = location;
		this.logo = logo;
		this.averageGrade = averageGrade;
		this.managerUsername = managerUsername;
		this.deleted = deleted;
	}
	@Override
	public String toString() {
		return "Restaurant [id=" + id + ", name=" + name + ", type=" + type + ", articles=" + articles + ", status="
				+ status + ", location=" + location + ", logo=" + logo + ", averageGrade=" + averageGrade
				+ ", managerUsername=" + managerUsername + ", deleted=" + deleted + "]";
	}

}
