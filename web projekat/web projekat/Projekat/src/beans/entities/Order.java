package beans.entities;

import java.io.Serializable;
import java.util.HashMap;

import beans.enums.OrderStatus;

public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; // <=10 characters
	private HashMap<String, Integer> orderedArticle;// name,number of every article
	private Restaurant restaurant;
	private String dateTimeOrder;
	private double price; // order price
	private String customerUsername;
	private OrderStatus status;
	private boolean deleted;
	private String delivererUsername;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<String, Integer> getOrderedArticle() {
		return orderedArticle;
	}

	public void setOrderedArticle(HashMap<String, Integer> orderedArticle) {
		this.orderedArticle = orderedArticle;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public String getDateTimeOrder() {
		return dateTimeOrder;
	}

	public void setDateTimeOrder(String dateTimeOrder) {
		this.dateTimeOrder = dateTimeOrder;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCustomerUsername() {
		return customerUsername;
	}

	public void setCustomerUsername(String customerUsername) {
		this.customerUsername = customerUsername;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getDelivererUsername() {
		return delivererUsername;
	}

	public void setDelivererUsername(String delivererUsername) {
		this.delivererUsername = delivererUsername;
	}

	public Order() {
	}

	public Order(String id, HashMap<String, Integer> orderedArticle, Restaurant restaurant, String dateTimeOrder,
			double price, String customerUsername, OrderStatus status,boolean deleted, String delivererUsername) {
		super();
		this.id = id;
		this.orderedArticle = orderedArticle;
		this.restaurant = restaurant;
		this.dateTimeOrder = dateTimeOrder;
		this.price = price;
		this.customerUsername = customerUsername;
		this.status = status;
		this.deleted = deleted;
		this.delivererUsername = delivererUsername;
	}

}
