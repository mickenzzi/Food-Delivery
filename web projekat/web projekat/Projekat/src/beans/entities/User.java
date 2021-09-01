package beans.entities;

import java.io.Serializable;
import java.util.ArrayList;

import beans.enums.CustomerType;
import beans.enums.Gender;
import beans.enums.UserRole;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Gender gender;
	private String dateOfBirth;
	private UserRole role;
	private ArrayList<Order> customerOrders;
	private Basket basket; // customer
	private Restaurant restaurant; // manager
	private ArrayList<Order> delivererOrders;
	private double collectedPoint;
	private CustomerType customerType;
	private double sale; // %
	private double requiredPoint;
	private boolean blocked;
	private boolean deleted;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public ArrayList<Order> getCustomerOrders() {
		return customerOrders;
	}

	public void setCustomerOrders(ArrayList<Order> customerOrders) {
		this.customerOrders = customerOrders;
	}

	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public ArrayList<Order> getDelivererOrders() {
		return delivererOrders;
	}

	public void setDelivererOrders(ArrayList<Order> delivererOrders) {
		this.delivererOrders = delivererOrders;
	}

	public double getCollectedPoint() {
		return collectedPoint;
	}

	public void setCollectedPoint(double collectedPoint) {
		this.collectedPoint = collectedPoint;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public double getSale() {
		return sale;
	}

	public void setSale(double sale) {
		this.sale = sale;
	}

	public double getRequiredPoint() {
		return requiredPoint;
	}

	public void setRequiredPoint(double requiredPoint) {
		this.requiredPoint = requiredPoint;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public User() {
	}

	public User(String username, String password, String firstName, String lastName, Gender gender, String dateOfBirth,
			UserRole role, ArrayList<Order> customerOrders, Basket basket, Restaurant restaurant,
			ArrayList<Order> delivererOrders, double collectedPoint, CustomerType customerType, double sale,
			double requiredPoint) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.role = role;
		this.customerOrders = customerOrders;
		this.basket = basket;
		this.restaurant = restaurant;
		this.delivererOrders = delivererOrders;
		this.collectedPoint = collectedPoint;
		this.customerType = customerType;
		this.sale = sale;
		this.requiredPoint = requiredPoint;
		this.blocked = false;
		this.deleted = false;
	}

}
