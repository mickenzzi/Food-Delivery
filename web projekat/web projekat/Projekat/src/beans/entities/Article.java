package beans.entities;

import java.io.Serializable;

import beans.enums.ArticleType;

public class Article implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private ArticleType type;
	private double price;
	private double quantity; // mm/gr
	private String description;
	private String image;
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

	public ArticleType getType() {
		return type;
	}

	public void setType(ArticleType type) {
		this.type = type;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Article() {
	}

	public Article(String id, String name, ArticleType type, double price, double quantity, String description,
			String image) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.price = price;
		this.quantity = quantity;
		this.description = description;
		this.image = image;
		this.deleted = false;
	}

	public Article(String name, ArticleType type, double price, double quantity, String description, String image) {
		super();
		this.name = name;
		this.type = type;
		this.price = price;
		this.quantity = quantity;
		this.description = description;
		this.image = image;
		this.deleted = false;
	}

}
