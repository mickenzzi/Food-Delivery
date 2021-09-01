package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import beans.entities.Article;
import beans.entities.User;
import beans.entities.Order;
import beans.entities.Restaurant;
import beans.enums.OrderStatus;

public class OrderDAO {
	public OrderDAO() {
		load();
	}

	private ArrayList<Order> orders = new ArrayList<Order>();

	private String envValue = System.getenv("FILE") + "\\WebContent\\data\\orders.json";
	int maxNumberOfRejectPerMonth = 5;

	private void load() {

		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(this.envValue);
		HashMap<String, Order> loadedOrders = new HashMap<String, Order>();
		try {
			loadedOrders = objectMapper.readValue(file, new TypeReference<HashMap<Integer, Order>>() {
			});

		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Order order : loadedOrders.values()) {
			orders.add(order);
		}
	}

	public void saveAll() {
		HashMap<String, Order> ordersMap = new HashMap<String, Order>();
		for (Order order : orders) {
			ordersMap.put(order.getId(), order);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(new FileOutputStream(envValue), ordersMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Order> findAll() {
		ArrayList<Order> ordersWithoutDeleted = new ArrayList<Order>();
		for(Order o : orders) {
			if(o.isDeleted() == false) {
				ordersWithoutDeleted.add(o);	
			}		
		}
		return ordersWithoutDeleted;
	}
	

	public Order add(User user, Restaurant restaurant, String dateTimeOrder) {
		String customerUsername = user.getUsername();
		HashMap<String, Integer> orderedArticle = orderedArticlesByRestaurant(restaurant, user);
		double price = calculatePrice(orderedArticle, restaurant, user);
		Order order = new Order(generateId(), orderedArticle, restaurant, dateTimeOrder, price, customerUsername,
				OrderStatus.IN_PROCESSING, false, null);
		orders.add(order);
		saveAll();
		return order;
	}
	//logic
	public boolean delete(String id) {
		for (Order order : orders) {
			if (order.getId().equals(id)) {
				order.setDeleted(true);
				order.setStatus(OrderStatus.REJECTED);
				;
				saveAll();
				return true;
			}
		}
		return false;
	}


	public String generateId() {
		boolean valid = false;
		int id = 1;
		load();
		for (id = 1; id <= orders.size(); id++) {
			for (Order order : orders) {
				if (Integer.parseInt(order.getId()) == id) {
					valid = true;
					break;
				}
			}
			if (!valid) {
				String zeros = "";
				int digits = String.valueOf(id).length();
				for (int i = 0; i < 10 - digits; i++) {
					zeros += "0";
				}
				return zeros + String.valueOf(id);
			}
			valid = false;
		}
		String zeros = "";
		int digits = String.valueOf(id).length();
		for (int i = 0; i < 10 - digits; i++) {
			zeros += "0";
		}
		return zeros + String.valueOf(id);
	}

	private double calculatePrice(HashMap<String, Integer> orderedArticleByRestaurant, Restaurant restaurant,
			User user) {
		double price = 0;
		for (Article restaurantArticle : restaurant.getArticles())
			for (String orderedArticleName : orderedArticleByRestaurant.keySet()) {
				if (restaurantArticle.getName().equals(orderedArticleName)) {
					double articlePrice = restaurantArticle.getPrice()
							* user.getBasket().getArticle().get(orderedArticleName);
					double priceWithSale = articlePrice - restaurantArticle.getPrice() * user.getSale();
					price += priceWithSale;
				}
			}
		return price;
	}

	private HashMap<String, Integer> orderedArticlesByRestaurant(Restaurant restaurant, User user) {
		HashMap<String, Integer> orderedArticleByRestaurant = new HashMap<String, Integer>();
		HashMap<String, Integer> orderedArticle = user.getBasket().getArticle();
		for (Article restaurantArticle : restaurant.getArticles()) {
			for (String articleName : orderedArticle.keySet()) { // praviimo niz stringova na osnovu kljuca narucenog
																	// artikla,odnosno naziva
				if (restaurantArticle.getName().equals(articleName)) {
					orderedArticleByRestaurant.put(articleName, orderedArticle.get(articleName));
				}
			}
		}
		return orderedArticleByRestaurant;
	}

	public int detectTroll(String customerUsername) {
		int count = 0;
		for (Order order : orders) {
			if (order.getCustomerUsername().equals(customerUsername)) {
				Date oneMonthAgo = oneMonthAgo();
				Date today = today();
				Date dateOrder = parseDate(order.getDateTimeOrder());
				if (dateOrder.after(oneMonthAgo) && dateOrder.before(today)) {
					if (order.isDeleted() == true) {
						count += 1;
					}
				}
			}
		}
		return count;
	}

	private Date parseDate(String dateTimeOrder) {
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date dateOrder = null;
		try {
			dateOrder = format.parse(dateTimeOrder);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateOrder;
	}

	private Date today() {
		Date monthAgo = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(monthAgo);
		c.add(Calendar.MONTH, 0);
		return c.getTime();
	}

	private Date oneMonthAgo() {
		Date monthAgo = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(monthAgo);
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	public void editCustomer(String oldUsername, String newUsername) {
		for (Order order : orders) {
			if (order.getCustomerUsername().equals(oldUsername)) {
				order.setCustomerUsername(newUsername);
			}
		}
	}
	
	public void deleteAllCustomer(String customerUsername) {
		for (Order order : orders) {
			if (order.getCustomerUsername().equals(customerUsername)) {
				order.setDeleted(true);
				;
			}
		}
		saveAll();
	}

	public void editDeliverer(String oldUsername, String newUsername) {
		for (Order order : orders) {
			if (order.getDelivererUsername() != null) {
				if (order.getCustomerUsername().equals(oldUsername)) {
					order.setDelivererUsername(newUsername);
					;
				}
			}
		}
		saveAll();
	}


}
