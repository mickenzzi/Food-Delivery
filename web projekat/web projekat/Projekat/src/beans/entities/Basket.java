package beans.entities;

import java.io.Serializable;
import java.util.HashMap;

public class Basket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> article; // name,number of every article
	private String customerUsername;
	private double price; // price for all articles in basket

	public HashMap<String, Integer> getArticle() {
		return article;
	}

	public void setArticle(HashMap<String, Integer> article) {
		this.article = article;
	}

	public String getCustomerUsername() {
		return customerUsername;
	}

	public void setCustomerUsername(String customerUsername) {
		this.customerUsername = customerUsername;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Basket() {

	}

	public Basket(HashMap<String, Integer> article, String customerUsername, double price) {
		super();
		this.article = article;
		this.customerUsername = customerUsername;
		this.price = price;
	}

}
