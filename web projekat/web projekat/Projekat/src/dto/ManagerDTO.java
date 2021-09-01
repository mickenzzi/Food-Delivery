package dto;

import beans.entities.Restaurant;
import beans.enums.Gender;
import beans.enums.UserRole;

public class ManagerDTO {
	public String username;
	public String password;
	public String firstName;
	public String lastName;
	public Gender gender;
	public String dateOfBirth;
	public UserRole userRole;
	public Restaurant restaurant; 
}
