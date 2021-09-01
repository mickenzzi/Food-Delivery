package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import beans.entities.Comment;
import beans.enums.CommentStatus;
import dto.CommentDTO;
import beans.entities.User;

public class CommentDAO {
	public CommentDAO() {
		load();
	}

	private ArrayList<Comment> comments = new ArrayList<Comment>();

	private String envValue = System.getenv("FILE") + "\\WebContent\\data\\comments.json";

	private void load() {
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(this.envValue);
		HashMap<Integer, Comment> loadedComments = new HashMap<Integer, Comment>();
		try {
			loadedComments = objectMapper.readValue(file, new TypeReference<HashMap<Integer, Comment>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Comment comment : loadedComments.values()) {
			if (!comment.isDeleted()) {
				comments.add(comment);
			}
		}
	}

	public void saveAll() {
		HashMap<Integer, Comment> commentsMap = new HashMap<Integer, Comment>();
		for (Comment comment : comments) {
			commentsMap.put(comment.getId(), comment);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
		ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(new FileOutputStream(envValue), commentsMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Comment> findAll() {
		ArrayList<Comment> commentsWithoutDeleted = new ArrayList<Comment>();
		for(Comment c : comments) {
			if(c.isDeleted() == false) {
				commentsWithoutDeleted.add(c);	
			}		
		}
		return commentsWithoutDeleted;
	}
	
	public void add(CommentDTO c, User user) {
		Comment newComment = new Comment(generateId(), CommentStatus.IN_PROCESSING, c.text, user.getUsername(),
				c.restaurantName, c.grade, false);
		comments.add(newComment);
		saveAll();
	}
	//logic
	public boolean delete(int id) {
		for (Comment comment : comments) {
			if (comment.getId() == id) {
				comment.setDeleted(true);
				;
				saveAll();
				return true;
			}
		}
		return false;
	}

	public int generateId() {
		boolean existId = false;
		int id = 1;
		load();
		for (id = 1; id <= comments.size(); id++) {
			for (Comment comment : comments) {
				if (comment.getId() == id) {
					existId = true;
					break;
				}
			}
			if (!existId) {
				return id;
			}
			existId = false;
		}
		return id;
	}

	public boolean exist(String restaurantName, String customerUsername) {
		for (Comment comment : comments) {
			if (comment.getCustomerUsername().equals(customerUsername) && comment.isDeleted() == false) {
				if (comment.getRestaurantName().equals(restaurantName)) {
					return true;
				}
			}
		}
		return false;
	}
}
