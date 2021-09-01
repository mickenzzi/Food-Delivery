package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import beans.entities.Article;
import beans.entities.User;
import beans.entities.Basket;
import beans.entities.Order;
import beans.entities.Restaurant;
import beans.enums.OrderStatus;
import beans.enums.ArticleType;
import beans.enums.CustomerType;
import beans.enums.Gender;
import beans.enums.UserRole;
import dto.ArticleDTO;
import dto.UserDTO;
import dto.BasketDTO;
import dto.EditUserDTO;
import dto.TrollDTO;

public class UserDAO {
	public UserDAO() {
		load();
	}

	private HashMap<String, User> users = new HashMap<String, User>();

	private String envValue = System.getenv("FILE") + "\\WebContent\\data\\users.json";

	public double maxBronze = 3000;
	public double maxSilver = 4000;
	public double saleSilver = 0.03;
	public double saleGold = 0.05;

	private void load() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
		File file = new File(this.envValue);
		HashMap<String, User> loadedUsers = new HashMap<String, User>();
		try {
			loadedUsers = objectMapper.readValue(file, new TypeReference<HashMap<String, User>>() {
			});

		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (User user : loadedUsers.values()) {
			users.put(user.getUsername(), user);
		}
	}

	public void saveAll() {

		// Java 8
		HashMap<String, User> usersMap = new HashMap<String, User>();
		for (User user : users.values()) {
			usersMap.put(user.getUsername(), user);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
		try {
			FileOutputStream fileOutput = new FileOutputStream(envValue);
			writer.writeValue(fileOutput, usersMap);
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<User> findAll() {
		ArrayList<User> usersWithoutDeleted = new ArrayList<User>();
		for (User u : users.values()) {
			if (u.isDeleted() == false) {
				usersWithoutDeleted.add(u);
			}
		}
		return usersWithoutDeleted;
	}

	public void add(User user) {
		users.put(user.getUsername(), user);
		saveAll();
	}

	public void delete(String username) {
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		for (Restaurant r : restaurantDAO.findAll()) {
			if (r.getManagerUsername() != null && r.getManagerUsername().equals(username)) {
				r.setManagerUsername(null);
				restaurantDAO.saveAll();
			}
		}
		for (User user : users.values()) {
			if (user.getUsername().equals(username)) {
				user.setDeleted(true);
				OrderDAO orderDAO = new OrderDAO();
				if (user.getRole().equals(UserRole.CUSTOMER)) {
					orderDAO.deleteAllCustomer(username);
				}

				saveAll();
				return;
			}
		}
	}

	public void addArticleBasket(User u, ArticleDTO a) {
		for (User user : users.values()) {
			if (user.getUsername().equals(u.getUsername())) {
				RestaurantDAO restaurantDAO = new RestaurantDAO();
				Restaurant restaurant = restaurantDAO.findById(a.restaurantId);
				Basket basket = user.getBasket();
				Article article = new Article(a.name, convertStringToArticleType(a.type), a.price, a.quantity,
						a.description, a.image);
				if (basket.getArticle() == null) {
					HashMap<String, Integer> articles = new HashMap<String, Integer>();
					double price = 0;
					price = priceWithSale(a, user);
					price = price + basket.getPrice();
					articles.put(article.getName(), a.numberOfArticle);
					basket.setArticle(articles);
					basket.setPrice(price);
					user.setRestaurant(restaurant);
				} else {
					for (Article art : restaurant.getArticles()) {
						if (art.getName().equals(a.name)) {
							int num = 0;
							if (basket.getArticle().get(a.name) != null) {
								num = basket.getArticle().get(a.name);
							}
							basket.getArticle().remove(a.name);
							basket.getArticle().put(article.getName(), num + a.numberOfArticle);
							double priceOneArticle = priceWithSale(a, user);
							priceOneArticle += basket.getPrice();
							basket.setPrice(priceOneArticle);
							user.setBasket(basket);
							user.setRestaurant(restaurant);
							saveAll();
							return;
						}
					}

					basket.getArticle().put(article.getName(), a.numberOfArticle);
					double priceOneArticle = priceWithSale(a, user);
					double price = basket.getPrice() + priceOneArticle;
					basket.setPrice(price);
					user.setRestaurant(restaurant);
				}
				user.setRestaurant(restaurant);
				user.setBasket(basket);
				saveAll();
				return;
			}
		}

	}

	private double priceWithSale(ArticleDTO a, User u) {
		double price = a.price * a.numberOfArticle;
		if (u.getCustomerType().equals(CustomerType.SILVER)) {
			return price = price - price * saleSilver;
		} else if (u.getCustomerType().equals(CustomerType.GOLD)) {
			return price = price - price * saleGold;
		}
		price = price - price * u.getSale();
		return price;
	}

	public User deleteBasket(User u) {
		for (User user : users.values()) {
			if (user.getUsername().equals(u.getUsername())) {
				user.setBasket(new Basket(null, user.getUsername(), 0));
				saveAll();
				return user;
			}
		}
		return null;
	}

	public ArrayList<BasketDTO> getBasketDTO(User u) {
		ArrayList<BasketDTO> baskets = new ArrayList<BasketDTO>();
		Restaurant restaurant = u.getRestaurant();
		System.out.println("Restoran je:" + restaurant.getName());
		for (User user : users.values()) {
			if (user.getUsername().equals(u.getUsername())) {
				Basket basket = user.getBasket();
				if (basket.getArticle() != null) {
					for (String articleName : basket.getArticle().keySet()) {
						for (Article article : restaurant.getArticles()) {
							if (article.getName().equals(articleName)) {
								BasketDTO basketDTO = new BasketDTO();
								basketDTO.name = article.getName();
								basketDTO.price = article.getPrice();
								basketDTO.image = article.getImage();
								basketDTO.totalPrice = user.getBasket().getPrice();
								basketDTO.numberOfArticle = user.getBasket().getArticle().get(article.getName());
								baskets.add(basketDTO);
							}
						}
					}
				}

			}
		}

		return baskets;
	}

	public User addOrder(User u) {
		for (User user : users.values()) {
			if (user.getUsername().equals(u.getUsername())) {
				String dateTimeOrder = makeDate();
				OrderDAO orderDAO = new OrderDAO();
				Order newOrder = orderDAO.add(user, user.getRestaurant(), dateTimeOrder);
				if (user.getCustomerOrders() == null) {
					user.setCustomerOrders(new ArrayList<Order>());
				}
				user.getCustomerOrders().add(newOrder);

				double collectedPoints = upgradePoints(user);
				user.setCollectedPoint(collectedPoints);
				setCustomerStatus(user);
				user.setBasket(new Basket(null, user.getUsername(), 0));
				saveAll();
				return user;
			}
		}
		return null;
	}

	/*
	 * private void findOrderRestaurant(ArrayList<Restaurant> restaurants1, String
	 * articleName) { RestaurantDAO restaurantDAO = new RestaurantDAO(); Restaurant
	 * restaurant = restaurantDAO.findByArticle(articleName); if (restaurant !=
	 * null) { boolean exist = false; for (Restaurant restaurant1 : restaurants1) {
	 * if (restaurant1.getId().equals(restaurant.getId())) { exist = true; } } if
	 * (!exist) { restaurants1.add(restaurant); } } }
	 */

	private String makeDate() {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		String dateTimeOrder = dateTime.format(formatter);
		return dateTimeOrder;
	}

	private double reducePoints(double orderPrice, User user) {
		double reducedPoints = orderPrice / 1000 * 133 * 4;
		double collectedPoints = user.getCollectedPoint() - reducedPoints;
		return collectedPoints;
	}

	private double upgradePoints(User user) {
		double points = user.getBasket().getPrice() / 1000 * 133;
		double collectedPoints = user.getCollectedPoint() + points;
		return collectedPoints;
	}

	private void setCustomerStatus(User user) {
		double points = user.getCollectedPoint();
		if (points > maxBronze && points <= maxSilver) {
			user.setCustomerType(CustomerType.SILVER);
			user.setSale(saleSilver);
			double requiredPoints = maxSilver - points;
			user.setRequiredPoint(requiredPoints);
		} else if (points <= maxBronze) {
			user.setCustomerType(CustomerType.BRONZE);
			double requiredPonts = maxBronze - points;
			user.setRequiredPoint(requiredPonts);
		} else if (points > maxSilver) {
			user.setCustomerType(CustomerType.GOLD);
			user.setSale(saleGold);
		}
	}

	public User deleteBasketArticle(User u, String articleName) {
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		for (User user : users.values()) {
			if (user.getUsername().equals(u.getUsername())) {
				Article article = restaurantDAO.findArticle(articleName);

				double artiklePrice = article.getPrice() * user.getBasket().getArticle().get(article.getName());
				double articleBasketPrice = artiklePrice - artiklePrice * u.getSale();
				double price = user.getBasket().getPrice() - articleBasketPrice;
				user.getBasket().getArticle().remove(article.getName());
				user.getBasket().setPrice(price);
				saveAll();
				return user;
			}
		}
		return null;
	}

	public User deleteOrder(User u, String idOrder) {
		for (User user : findAll()) {
			if (user.getUsername().equals(u.getUsername())) {
				ArrayList<Order> orders = user.getCustomerOrders();
				if (orders != null) {
					for (Order order : user.getCustomerOrders()) {
						if (order.getId().equals(idOrder) && order.getStatus().equals(OrderStatus.IN_PROCESSING)) {
							order.setDeleted(true);
							order.setStatus(OrderStatus.REJECTED);
							double collectedPoints = reducePoints(order.getPrice(), user);
							user.setCollectedPoint(collectedPoints);
							setCustomerStatus(user);
							saveAll();
							return user;
						}
					}
				}
			}
		}
		return null;
	}

	public OrderStatus convertStringToOrderStatus(String status) {
		if (status.equals("DELIVERED")) {
			return OrderStatus.DELIVERED;
		} else if (status.equals("IN_PREPARATION")) {
			return OrderStatus.IN_PREPARATION;
		} else if (status.equals("IN_PROCESSING")) {
			return OrderStatus.IN_PROCESSING;
		} else if (status.equals("ON_HOLD")) {
			return OrderStatus.ON_HOLD;
		} else if (status.equals("REJECTED")) {
			return OrderStatus.REJECTED;
		} else if (status.equals("REQUEST_SENT")) {
			return OrderStatus.REQUEST_SENT;
		} else {
			return OrderStatus.SENT;
		}
	}

	public String convertOrderStatusToString(OrderStatus status) {
		if (status.equals(OrderStatus.DELIVERED)) {
			return "DELIVERED";
		} else if (status.equals(OrderStatus.IN_PREPARATION)) {
			return "IN_PREPARATION";
		} else if (status.equals(OrderStatus.IN_PROCESSING)) {
			return "IN_PROCESSING";
		} else if (status.equals(OrderStatus.ON_HOLD)) {
			return "ON_HOLD";
		} else if (status.equals(OrderStatus.REJECTED)) {
			return "REJECTED";
		} else if (status.equals(OrderStatus.REQUEST_SENT)) {
			return "REQUEST_SENT";
		} else {
			return "SENT";
		}
	}

	public ArticleType convertStringToArticleType(String type) {
		if (type.equals("FOOD")) {
			return ArticleType.FOOD;
		} else {
			return ArticleType.DRINK;
		}
	}

	public String convertArticleTypeToString(ArticleType type) {
		if (type.equals(ArticleType.DRINK)) {
			return "FOOD";
		} else {
			return "DRINK";
		}
	}

	public Gender convertStringToGender(String gender) {
		if (gender.equals("MUSKI")) {
			return Gender.MALE;
		} else {
			return Gender.FEMALE;
		}

	}

	public String convertGenderToString(Gender gender) {
		if (gender.equals(Gender.MALE)) {
			return "MUSKI";
		} else {
			return "ZENSKI";
		}
	}

	public String convertUserRoleToString(UserRole role) {
		if (role.equals(UserRole.CUSTOMER)) {
			return "CUSTOMER";
		} else if (role.equals(UserRole.DELIVERER)) {
			return "DELIVERER";
		} else if (role.equals(UserRole.MANAGER)) {
			return "MANAGER";
		} else {
			return "ADMINISTRATOR";
		}
	}

	public UserRole convertStringToUserRole(String role) {
		if (role.equals("ADMINISTRATOR")) {
			return UserRole.ADMINISTRATOR;
		} else if (role.equals("MANAGER")) {
			return UserRole.MANAGER;
		} else if (role.equals("DELIVERER")) {
			return UserRole.DELIVERER;
		} else {
			return UserRole.CUSTOMER;
		}
	}

	public String convertCustomerTypeToString(CustomerType type) {
		if (type.equals(CustomerType.GOLD)) {
			return "GOLD";
		} else if (type.equals(CustomerType.SILVER)) {
			return "SILVER";
		} else {
			return "BRONZE";
		}
	}

	public CustomerType convertStringToCustomerType(String type) {
		if (type.equals("GOLD")) {
			return CustomerType.GOLD;
		} else if (type.equals("SILVER")) {
			return CustomerType.SILVER;
		} else {
			return CustomerType.BRONZE;
		}
	}

	public User editUser(UserDTO u, String oldUsername) {
		for (User user : users.values()) {
			if (user.getUsername().equals(oldUsername)) {

				if (!user.getUsername().equals(u.username)) {
					System.out.println("Username is edited.");
				}
				user.setUsername(u.username);
				user.setPassword(u.password);
				user.setFirstName(u.firstName);
				user.setLastName(u.lastName);
				user.setGender(convertStringToGender(u.gender));
				user.setDateOfBirth(u.dateOfBirth);
				user.getBasket().setCustomerUsername(u.username);
				OrderDAO orderDAO = new OrderDAO();
				if (user.getRole().equals(UserRole.CUSTOMER)) {
					orderDAO.editCustomer(oldUsername, user.getUsername());
				}
				if (user.getRole().equals(UserRole.DELIVERER)) {
					orderDAO.editDeliverer(oldUsername, user.getUsername());
				}
				if (user.getRole().equals(UserRole.MANAGER)) {
					RestaurantDAO restaurantDAO = new RestaurantDAO();
					for (Restaurant r : restaurantDAO.findAll()) {
						if (r.getManagerUsername().equals(oldUsername) && !oldUsername.equals(u.username)) {
							r.setManagerUsername(u.username);
							restaurantDAO.saveAll();
						}
					}
				}
				for (User user1 : users.values()) {
					if (user1.getCustomerOrders() != null) {
						for (Order customerOrder : user1.getCustomerOrders()) {
							if (customerOrder.getCustomerUsername().equals(oldUsername)) {
								customerOrder.setCustomerUsername(u.username);
							}
							if (customerOrder.getCustomerUsername() != null) {
								if (customerOrder.getDelivererUsername().equals(oldUsername)) {
									customerOrder.setDelivererUsername(u.username);
								}
							}
						}
					}
					if (user1.getDelivererOrders() != null) {
						for (Order delivererOrder : user1.getDelivererOrders()) {
							if (delivererOrder.getCustomerUsername().equals(oldUsername)) {
								delivererOrder.setCustomerUsername(u.username);
							}
							if (delivererOrder.getDelivererUsername() != null) {
								if (delivererOrder.getDelivererUsername().equals(oldUsername)) {
									delivererOrder.setDelivererUsername(u.username);
								}
							}
						}
					}
				}
				saveAll();
				return user;
			}
		}
		return null;
	}

	public User editOldUser(EditUserDTO u, String oldUsername) {

		for (User user : users.values()) {
			if (user.getUsername().equals(oldUsername)) {
				user.setUsername(u.username);
				user.setPassword(u.password);
				user.setFirstName(u.firstName);
				user.setLastName(u.lastName);
				user.setGender(convertStringToGender(u.gender));
				user.setDateOfBirth(u.dateOfBirth);
				user.getBasket().setCustomerUsername(u.username);
				OrderDAO orderDAO = new OrderDAO();
				if (user.getRole().equals(UserRole.CUSTOMER)) {
					orderDAO.editCustomer(oldUsername, user.getUsername());
				}
				if (user.getRole().equals(UserRole.DELIVERER)) {
					orderDAO.editDeliverer(oldUsername, user.getUsername());
				}
				if (user.getRole().equals(UserRole.MANAGER)) {
					RestaurantDAO restaurantDAO = new RestaurantDAO();
					for (Restaurant r : restaurantDAO.findAll()) {
						if (r.getManagerUsername().equals(oldUsername) && !oldUsername.equals(u.username)) {
							r.setManagerUsername(u.username);
							restaurantDAO.saveAll();
						}
					}
				}
				for (User user1 : users.values()) {
					if (user1.getCustomerOrders() != null) {
						for (Order customerOrder : user1.getCustomerOrders()) {
							if (customerOrder.getCustomerUsername().equals(oldUsername)) {
								customerOrder.setCustomerUsername(u.username);
							}
							if (customerOrder.getCustomerUsername() != null) {
								if (customerOrder.getDelivererUsername().equals(oldUsername)) {
									customerOrder.setDelivererUsername(u.username);
								}
							}
						}
					}
					if (user1.getDelivererOrders() != null) {
						for (Order delivererOrder : user1.getDelivererOrders()) {
							if (delivererOrder.getCustomerUsername().equals(oldUsername)) {
								delivererOrder.setCustomerUsername(u.username);
							}
							if (delivererOrder.getDelivererUsername() != null) {
								if (delivererOrder.getDelivererUsername().equals(oldUsername)) {
									delivererOrder.setDelivererUsername(u.username);
								}
							}
						}
					}
				}
				saveAll();
				return user;
			}
		}
		return null;
	}

	public ArrayList<TrollDTO> findAllTrolls() {
		OrderDAO orderDAO = new OrderDAO();
		ArrayList<TrollDTO> trolls = new ArrayList<TrollDTO>();
		for (User user : users.values()) {
			if (orderDAO.detectTroll(user.getUsername()) >= orderDAO.maxNumberOfRejectPerMonth) {
				TrollDTO troll = new TrollDTO();
				troll.customerUsername = user.getUsername();
				troll.counterRejecetedOrder = orderDAO.detectTroll(user.getUsername());
				troll.blocked = user.isBlocked();
				trolls.add(troll);
			}
		}
		return trolls;
	}

	public void blockUser(String username) {
		System.out.println(username);
		for (User user : users.values()) {
			if (user.getUsername().equals(username)) {
				if (user.isBlocked() == false) {
					user.setBlocked(true);
					saveAll();
					break;
				}
			}
		}
	}

	public void unblockUser(String username) {
		for (User user : users.values()) {
			if (user.getUsername().equals(username)) {
				if (user.isBlocked() == true) {
					user.setBlocked(false);
					saveAll();
					break;
				}
			}
		}
	}

	public ArrayList<User> getAllManagersWithoutRestaurant() {
		ArrayList<User> managerWithoutRestaurants = new ArrayList<User>();
		for (User user : users.values()) {
			if (user.getRole() == UserRole.MANAGER && user.isDeleted() == false && user.getRestaurant() == null
					&& user.isBlocked() == false) {
				managerWithoutRestaurants.add(user);
			}
		}
		return managerWithoutRestaurants;
	}
}
