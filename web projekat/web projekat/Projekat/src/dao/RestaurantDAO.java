package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.entities.Article;
import beans.entities.Restaurant;
import beans.enums.RestaurantStatus;

public class RestaurantDAO {
	public RestaurantDAO() {
		load();
	}

	private HashMap<String, Restaurant> restaurants = new HashMap<String, Restaurant>();

	private String envValue = System.getenv("FILE") + "\\WebContent\\data\\restaurants.json";

	private void load() {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
		File file = new File(this.envValue);
		
		HashMap<String, Restaurant> loadedRestaurants = new HashMap<String, Restaurant>();
		try {
			loadedRestaurants = objectMapper.readValue(file, new TypeReference<HashMap<String, Restaurant>>() {
			});

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Restaurant restaurant : loadedRestaurants.values()) {
				restaurants.put(restaurant.getId(), restaurant);
		}
	}

	public void saveAll() {
		HashMap<String, Restaurant> restaurantsMap = new HashMap<String, Restaurant>();
		for (Restaurant restaurant : restaurants.values()) {
			restaurantsMap.put(restaurant.getId(), restaurant);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			FileOutputStream fileOut = new FileOutputStream(this.envValue);
			objectMapper.writeValue(fileOut, restaurantsMap);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Restaurant> findAll() {
		ArrayList<Restaurant> restaurantsWithoutDeleted= new ArrayList<Restaurant>();
		for(Restaurant u : restaurants.values()) {
			if(u.isDeleted() == false) {
				restaurantsWithoutDeleted.add(u);	
			}		
		}
		return restaurantsWithoutDeleted;		
	}

	public void add(Restaurant restaurant) {
		this.restaurants.put(restaurant.getId(), restaurant);
		System.out.println(this.restaurants.size() + "h");
		saveAll();
	}

	// logic
	public boolean delete(String id) {
		for (String restaurantId : restaurants.keySet()) {
			if (restaurantId.equals(id)) {
				restaurants.get(id).setDeleted(true);
				saveAll();
				return true;
			}
		}
		return false;
	}

	public Restaurant findById(String id) {
		for (Restaurant restaurant : restaurants.values()) {
			if (restaurant.getId().equals(id)) {
				return restaurant;
			}
		}
		return null;
	}

	public Collection<Restaurant> findAllSorted() {
		Collection<Restaurant> openRestaurants = new ArrayList<Restaurant>();
		Collection<Restaurant> closedRestaurants = new ArrayList<Restaurant>();

		load();
		for (Restaurant restaurant : restaurants.values()) {
			if (restaurant.getStatus().equals(RestaurantStatus.OPEN)) {
				openRestaurants.add(restaurant);
			} else {
				closedRestaurants.add(restaurant);
			}
		}
		// two list,one for closed,one for open restaurants
		// when we add open restaurants,after that we add closed
		// sort-first open then closed
		for (Restaurant restaurant : closedRestaurants) {
			openRestaurants.add(restaurant);
		}

		return openRestaurants;
	}

	public Restaurant findByArticle(String articleName) {
		load();
		for (Restaurant restaurant : restaurants.values()) {
			for (Article article : restaurant.getArticles()) {
				if (article.getName().equals(articleName)) {
					return restaurant;
				}
			}
		}
		return null;
	}

	public Article findArticle(String articleName) {
		RestaurantDAO dao = new RestaurantDAO();
		for (Restaurant restaurant : dao.findAll()) {
			for (Article article : restaurant.getArticles()) {
				if (article.getName().equals(articleName)) {
					return article;
				}
			}
		}
		return null;
	}
}
