package dto;

import java.util.ArrayList;
import java.util.List;

import beans.entities.Article;
import beans.entities.Restaurant;
import beans.enums.OrderStatus;

public class OrderListDTO {
	public String id;
	public ArrayList<Article> orderedArticle;
	public Restaurant restaurant;
	public String dateTimeOrder;
	public double price;
	public String status;
	public List<Integer> numberOfOrder;
	public boolean deleted;
	public String customerUsername;
	public String delivererUsername;
}
