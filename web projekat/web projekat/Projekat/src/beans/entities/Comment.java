package beans.entities;

import java.io.Serializable;

import beans.enums.CommentStatus;

public class Comment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private CommentStatus status;
	private String text;
	private String customerUsername;
	private String restaurantName;
	private int grade; // 1-5
	private boolean approved;
	private boolean deleted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CommentStatus getStatus() {
		return status;
	}

	public void setStatus(CommentStatus status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCustomerUsername() {
		return customerUsername;
	}

	public void setCustomerUsername(String customerUsername) {
		this.customerUsername = customerUsername;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Comment() {
	}

	public Comment(int id, CommentStatus status, String text, String customerUsername, String restaurantName, int grade,
			boolean approved) {
		super();
		this.id = id;
		this.status = status;
		this.text = text;
		this.customerUsername = customerUsername;
		this.restaurantName = restaurantName;
		this.grade = grade;
		this.approved = approved;
		this.deleted = false;
	}

}
