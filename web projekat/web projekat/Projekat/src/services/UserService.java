package services;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;

import beans.entities.Article;
import beans.entities.User;
import beans.entities.Basket;
import beans.entities.Order;
import beans.enums.CustomerType;
import beans.enums.UserRole;
import dao.UserDAO;
import dto.UserDTO;
import dto.UserDataDTO;
import dto.ManagerDTO;
import dto.OrderListDTO;
import dto.EditUserDTO;
import dto.LoginDTO;
import dto.TrollDTO;
import dto.WorkerDTO;

@Path("/users")
public class UserService {
	@Context
	ServletContext context;

	@POST
	@Path("/registrationCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registrationCustomer(@Context HttpServletRequest request, UserDTO u) {
		UserDAO userDAO = new UserDAO();
		for (User user : userDAO.findAll()) {
			if (user.getUsername().equals(u.username)) {
				System.out.println("The task \"registration customer\" failed.");
				return Response.status(400).entity("The task \"registration customer\" failed.").build();
			}
		}
		newCustomer(request, u);
		System.out.println("The task \"registration customer\" was successfully completed.");
		return Response.status(200).build();
	}

	@POST
	@Path("/registrationWorker")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registrationWorker(@Context HttpServletRequest request, WorkerDTO w) {
		UserDAO userDAO = new UserDAO();
		request.getSession().invalidate();
		for (User user : userDAO.findAll()) {
			if (user.getUsername().equals(w.username)) {
				System.out.println("The task \"registration worker\" failed.");
				return Response.status(400).entity("The task \"registration worker\" failed.").build();
			}
		}
		if (w.userRole.equals("MANAGER")) {
			newManager(request, w, userDAO);
			System.out.println("The task \"registration manager\" was successfully completed.");
			return Response.status(200).build();
		} else if (w.userRole.equals("DELIVERER")) {
			newDeliverer(request, w, userDAO);
			System.out.println("The task \"registration deliverer\" was successfully completed.");
			return Response.status(200).build();
		}
		System.out.println("Error");
		return Response.status(400).build();
	}

	private void newCustomer(HttpServletRequest request, UserDTO u) {
		UserDAO userDAO = new UserDAO();
		User user = new User(u.username, u.password, u.firstName, u.lastName, userDAO.convertStringToGender(u.gender),
				u.dateOfBirth, UserRole.CUSTOMER, null, null, null, null, 0, CustomerType.BRONZE, 0, userDAO.maxBronze);
		Basket basket = new Basket(null, user.getUsername(), 0);
		user.setBasket(basket);
		userDAO.add(user);
	}

	private void newManager(HttpServletRequest request, WorkerDTO w, UserDAO userDAO) {
		User user = new User(w.username, w.password, w.firstName, w.lastName, userDAO.convertStringToGender(w.gender),
				w.dateOfBirth, UserRole.MANAGER, null, null, null, null, 0.0, CustomerType.BRONZE, 0.0,
				userDAO.maxBronze);
		Basket basket = new Basket(null, user.getUsername(), 0);
		user.setBasket(basket);
		userDAO.add(user);
	}

	private void newDeliverer(HttpServletRequest request, WorkerDTO w, UserDAO userDAO) {
		User user = new User(w.username, w.password, w.firstName, w.lastName, userDAO.convertStringToGender(w.gender),
				w.dateOfBirth, UserRole.DELIVERER, null, null, null, new ArrayList<Order>(), 0.0, CustomerType.BRONZE,
				0.0, userDAO.maxBronze);
		Basket basket = new Basket(null, user.getUsername(), 0);
		user.setBasket(basket);
		userDAO.add(user);
	}

	@POST
	@Path("/loginUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(@Context HttpServletRequest request, LoginDTO l) {
		UserDAO userDAO = new UserDAO();
		request.getSession().invalidate();
		for (User user : userDAO.findAll()) {
			if (user.getUsername().equals(l.username)) {
				if (user.isBlocked() == true) {
					System.out.println("User is blocked.");
				} else {
					if (user.getPassword().equals(l.password)) {
						System.out.println("The task \"login user\" was successfully completed.");
						request.getSession().setAttribute("user", user);
						return Response.status(200).build();
					}
				}
			}
		}
		System.out.println("The task \"login user\" failed.");
		return Response.status(400).entity("The task \"login user\" failed.").type(MediaType.TEXT_PLAIN).build();
	}

	@GET
	@Path("/logOutUser")
	public void logOutUser(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		user.setRestaurant(null);
		if (user != null) {
			System.out.println("Log out " + user.getUsername());
		}
		request.getSession().invalidate();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getUsers() {
		UserDAO userDAO = new UserDAO();
		return userDAO.findAll();
	}

	@GET
	@Path("/getLoginUser")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getLoginUser(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return null;
		}
		UserDTO userDTO = getUserDTO(user);
		System.out.println("Prijavljeni korisnik: " + userDTO.username);
		return userDTO;
	}

	private UserDTO getUserDTO(User user) {
		UserDAO userDAO = new UserDAO();
		UserDTO userDTO = new UserDTO();
		userDTO.username = user.getUsername();
		userDTO.password = user.getPassword();
		userDTO.firstName = user.getFirstName();
		userDTO.lastName = user.getLastName();
		userDTO.gender = userDAO.convertGenderToString(user.getGender());
		userDTO.dateOfBirth = user.getDateOfBirth();
		return userDTO;
	}

	@GET
	@Path("/getUserData")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDataDTO getUserData(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return null;
		}
		UserDataDTO userDTO = getUserDataDTO(user);
		return userDTO;
	}

	private UserDataDTO getUserDataDTO(User user) {
		UserDataDTO dto = new UserDataDTO();
		UserDAO userDAO = new UserDAO();
		dto.username = user.getUsername();
		dto.userRole = userDAO.convertUserRoleToString(user.getRole());
		dto.password = user.getPassword();
		dto.firstName = user.getFirstName();
		dto.lastName = user.getLastName();
		dto.dateOfBirth = user.getDateOfBirth();
		dto.gender = userDAO.convertGenderToString(user.getGender());
		dto.customerType = userDAO.convertCustomerTypeToString(user.getCustomerType());
		dto.collectedPoints = user.getCollectedPoint();
		dto.sale = user.getSale();
		dto.requiredPoints = user.getRequiredPoint();
		return dto;
	}

	@GET
	@Path("/getManagers")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getManagers() {
		ArrayList<User> managers = new ArrayList<User>();
		UserDAO userDAO = new UserDAO();
		for (User u : userDAO.findAll()) {
			if (u.getRole().equals(UserRole.MANAGER) && u.getRestaurant() == null) {
				managers.add(u);
			}
		}

		return managers;
	}

	@GET
	@Path("/getManager")
	@Produces(MediaType.APPLICATION_JSON)
	public ManagerDTO getManager(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("User");
		if (user == null) {
			return null;
		}
		ManagerDTO managerDTO = getManagerDTO(user);
		return managerDTO;
	}

	private ManagerDTO getManagerDTO(User user) {
		ManagerDTO managerDTO = new ManagerDTO();
		managerDTO.username = user.getUsername();
		managerDTO.password = user.getPassword();
		managerDTO.dateOfBirth = user.getDateOfBirth();
		managerDTO.firstName = user.getFirstName();
		managerDTO.lastName = user.getLastName();
		managerDTO.gender = user.getGender();
		managerDTO.userRole = UserRole.MANAGER;
		managerDTO.restaurant = user.getRestaurant();
		return managerDTO;
	}

	@DELETE
	@Path("/deleteUser{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteUser(@PathParam("username") String username) {
		UserDAO userDAO = new UserDAO();
		userDAO.delete(username);
	}

	@GET
	@Path("/getOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<OrderListDTO> getOrders(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		UserDAO userDAO = new UserDAO();
		if (user == null) {
			System.out.println("User is unavaliable.");
			return null;
		}
		ArrayList<OrderListDTO> orderListDTO = new ArrayList<OrderListDTO>();
		if (user.getCustomerOrders() == null) {
			user.setCustomerOrders(new ArrayList<Order>());
		}
		for (Order o : user.getCustomerOrders()) {
			OrderListDTO dto = new OrderListDTO();
			dto.id = o.getId();
			dto.restaurant = o.getRestaurant();
			dto.dateTimeOrder = o.getDateTimeOrder();
			dto.status = userDAO.convertOrderStatusToString(o.getStatus());

			ArrayList<Article> orderedArticleKeys = new ArrayList<Article>();
			ArrayList<Integer> orderedArticleValues = new ArrayList<Integer>();
			for (Article article : o.getRestaurant().getArticles()) {
				for (String orderedArticleName : o.getOrderedArticle().keySet()) {
					if (article.getName().equals(orderedArticleName)) {
						orderedArticleKeys.add(article);
						orderedArticleValues.add(o.getOrderedArticle().get(orderedArticleName));
					}
				}
			}
			dto.price = o.getPrice();
			dto.orderedArticle = orderedArticleKeys;
			dto.numberOfOrder = orderedArticleValues;
			dto.deleted = o.isDeleted();
			orderListDTO.add(dto);
		}
		user.setBasket(new Basket(new HashMap<String, Integer>(), user.getUsername(), 0.0));
		request.getSession().setAttribute("user", user);
		return orderListDTO;
	}

	@GET
	@Path("/getCustomer{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getCustomer(@PathParam("id") String id) {
		UserDAO userDAO = new UserDAO();
		ArrayList<User> customers = new ArrayList<User>();

		for (User u : userDAO.findAll()) {
			if (u.getRole().equals(UserRole.CUSTOMER) && u.getCustomerOrders() != null) {
				for (Order o : u.getCustomerOrders()) {
					if (o.getRestaurant().getId().equals(id) && !customers.contains(u)) {
						customers.add(u);
					}
				}
			}
		}
		return customers;
	}

	@GET
	@Path("/getFilterUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArrayList<User> getFilterUsers(@Context HttpServletRequest request) {
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String username = request.getParameter("username");
		UserDAO userDAO = new UserDAO();
		ArrayList<User> filterUsers = new ArrayList<User>();
		for (User u : userDAO.findAll()) {
			if (u.getFirstName().contains(firstName) && u.getLastName().contains(lastName)
					&& u.getUsername().contains(username)) {
				filterUsers.add(u);
			}
		}
		return filterUsers;
	}

	@PUT
	@Path("/editUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editUser(@Context HttpServletRequest request, UserDTO u) {
		UserDAO userDAO = new UserDAO();
		User oldUser = (User) request.getSession().getAttribute("user");
		User user = userDAO.editUser(u, oldUser.getUsername());
		if (user == null) {
			System.out.println("The task \"edit user\" failed.");
			return Response.status(400).entity("The task \"edit user\" failed.").build();
		}
		request.getSession().setAttribute("user", user);
		System.out.println("The task \"edit user\" was successfully completed.");
		return Response.status(200).build();
	}

	@PUT
	@Path("/editLoggedUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editLoggedUser(@Context HttpServletRequest request, EditUserDTO u) {
		UserDAO userDAO = new UserDAO();
		User editUser = userDAO.editOldUser(u, u.oldUsername);
		request.getSession().setAttribute("user", editUser);
		System.out.println("The task \"edit user\" was successfully completed.");
		return Response.status(200).build();
	}

	@GET
	@Path("/getTrolls")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArrayList<TrollDTO> getTrolls() {
		UserDAO userDAO = new UserDAO();
		return userDAO.findAllTrolls();
	}

	@GET
	@Path("/getAllManagersWithoutRestaurant")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArrayList<User> getAllManagersWithoutRestauarant() {
		UserDAO userDAO = new UserDAO();
		return userDAO.getAllManagersWithoutRestaurant();
	}

	@POST
	@Path("/blockUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void blockUser(TrollDTO trollDTO) {
		System.out.println(trollDTO.customerUsername);
		UserDAO userDAO = new UserDAO();
		userDAO.blockUser(trollDTO.customerUsername);
	}

	@POST
	@Path("/unblockUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unblockUser(TrollDTO trollDTO) {
		UserDAO userDAO = new UserDAO();
		userDAO.unblockUser(trollDTO.customerUsername);
	}

}
