package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import beans.entities.Article;
import beans.entities.Comment;
import beans.entities.User;
import beans.entities.Location;
import beans.entities.Restaurant;
import beans.entities.Order;
import beans.enums.OrderStatus;
import dao.CommentDAO;
import dao.UserDAO;
import dao.RestaurantDAO;
import dao.OrderDAO;
import dto.ArticleDTO;
import dto.OrderListDTO;

@Path("/restaurants")
public class RestaurantService {
	@Context
	ServletContext context;

	@PostConstruct
	public void init() {
		if (context.getAttribute("restaurant") == null) {
			context.setAttribute("restaurant", new RestaurantDAO());
		}
	}

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Restaurant> getAll() {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		return restaurantDAO.findAllSorted();
	}

	@GET
	@Path("/getById{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Restaurant getById(@PathParam("id") String id) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");

		for (Restaurant restaurant : restaurantDAO.findAllSorted()) {
			if (restaurant.getId().equals(id)) {
				return restaurant;
			}
		}
		return null;
	}
	
	@GET
	@Path("/getAllRest")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Restaurant> getAllRest() {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");	
		return restaurantDAO.findAll();
	}

	// restaurant id
	@GET
	@Path("/getArticlesByRestaurantId{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Article> getArticlesByRestaurantId(@PathParam("id") String id) {
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		ArrayList<Article> articles = new ArrayList<Article>();

		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(id)) {
				if (restaurant.getArticles() != null) {
					for (Article article : restaurant.getArticles()) {
						if (!article.isDeleted()) {
							articles.add(article);
						}
					}
				}

			}
		}

		return articles;
	}

	@POST
	@Path("/addRestaurant")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addRestaurant(Restaurant restaurant) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");

		for (Restaurant r : restaurantDAO.findAll()) {
			if (r.getName().equals(restaurant.getName()) && r.isDeleted() == false) {
				System.out.println("The task \"add restaurant\" failed.");
				return Response.status(400).entity("The task \"add restaurant\" failed.").build();
			}
		}

		ArrayList<Article> articles = new ArrayList<Article>();
		Location location = new Location(restaurant.getLocation().getLongitude(),
				restaurant.getLocation().getLatitude(), restaurant.getLocation().getAdress());
		Restaurant newRestaurant = new Restaurant(generateId(), restaurant.getName(), restaurant.getType(), articles,
				restaurant.getStatus(), location, restaurant.getLogo(), restaurant.getAverageGrade(),
				restaurant.getManagerUsername(), false);

		restaurantDAO.add(newRestaurant);
		addManagerToRestaurant(restaurant, newRestaurant);

		System.out.println("The task \"add restaurant\" was successfully completed.");
		return Response.status(200).build();
	}

	public String generateId() {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");

		boolean exist = false;
		int id = 1;
		for (id = 1; id <= restaurantDAO.findAll().size(); id++) {
			for (Restaurant r : restaurantDAO.findAll()) {
				if (Integer.parseInt(r.getId()) == id) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				return Integer.toString(id);
			}
			exist = false;
		}

		return Integer.toString(id);
	}

	public void addManagerToRestaurant(Restaurant oldRestaurant, Restaurant newRestaurant) {
		UserDAO userDAO = new UserDAO();
		for (User user : userDAO.findAll()) {
			if (user.getUsername().equals(oldRestaurant.getManagerUsername())) {
				user.setRestaurant(newRestaurant);
				userDAO.saveAll();
			}
		}
	}

	@POST
	@Path("/addArticleRestaurant")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addArticleRestaurant(@Context HttpServletRequest request, ArticleDTO articleDTO) {
		User user = (User) request.getSession().getAttribute("user");
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(user.getRestaurant().getId())) {
				for (Article article : restaurant.getArticles()) {
					if (articleDTO.name.equals(article.getName()) && !article.isDeleted()) {
						System.out.println("The task \"add article to restaurant\" failed.");
						return Response.status(400).entity("The task \"add article to restaurant\" failed.").build();
					}
				}
			}
		}
		UserDAO userDAO = new UserDAO();
		Article article = new Article(generateArticleId(user.getRestaurant().getId()), articleDTO.name, userDAO.convertStringToArticleType(articleDTO.type),
				articleDTO.price, articleDTO.quantity, articleDTO.description, articleDTO.image);
		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(user.getRestaurant().getId())) {
				ArrayList<Article> articles = restaurant.getArticles();
				articles.add(article);
				restaurant.setArticles(articles);
				user.setRestaurant(restaurant);
				restaurantDAO.saveAll();
			}
		}
		System.out.println("The task \"add article to restaurant\" was succesfully completed.");
		return Response.status(200).build();
	}

	public String generateArticleId(String restaurantId) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");

		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(restaurantId)) {
				if (restaurant.getArticles().size() == 0) {
					return "1";
				} else {
					int size = restaurant.getArticles().size();
					return String.valueOf(size + 1);
				}
			}
		}

		return "";
	}

	@GET
	@Path("/getArticleById{articleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArticleDTO getArticleById(@Context HttpServletRequest request, @PathParam("articleId") String id) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		User user = (User) request.getSession().getAttribute("user");

		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(user.getRestaurant().getId())) {
				for (Article article : restaurant.getArticles()) {
					if (article.getId().equals(id)) {
						ArticleDTO articleDTO = getArticleDTO(article);
						return articleDTO;
					}
				}
			}
		}

		return null;
	}

	private ArticleDTO getArticleDTO(Article article) {
		ArticleDTO articleDTO = new ArticleDTO();
		UserDAO userDAO = new UserDAO();
		articleDTO.name = article.getName();
		articleDTO.price = article.getPrice();
		articleDTO.type = userDAO.convertArticleTypeToString(article.getType());
		articleDTO.quantity = article.getQuantity();
		articleDTO.description = article.getDescription();
		articleDTO.image = article.getImage();
		articleDTO.id = article.getId();
		return articleDTO;
	}

	@POST
	@Path("/editArticle")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editArticle(@Context HttpServletRequest request, ArticleDTO articleDTO) {
		User user = (User) request.getSession().getAttribute("user");
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		UserDAO userDAO = new UserDAO();
		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getManagerUsername().equals(user.getUsername())) {
				for (Article article : restaurant.getArticles()) {
					if (articleDTO.id.equals(article.getId())) {
						if (!uniqueArticleName(articleDTO.name, restaurant.getId(), article.getId())) {
							System.out.println("The task \"edit article\" failed.");
							return Response.status(400).entity("The task \"edit article\" failed.").build();
						}

						article.setName(articleDTO.name);
						article.setPrice(articleDTO.price);
						article.setType(userDAO.convertStringToArticleType(articleDTO.type));
						article.setQuantity(articleDTO.quantity);
						article.setDescription(articleDTO.description);
						article.setImage(articleDTO.image);
						restaurantDAO.saveAll();
					}
				}
			}
		}
		System.out.println("The task \"edit article\" was successfully completed.");
		return Response.status(200).build();
	}

	public boolean uniqueArticleName(String articleName, String restaurantId, String articleId) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(restaurantId)) {
				for (Article article : restaurant.getArticles()) {
					if (article.getName().equals(articleName) && !(article.getId().equals(articleId))
							&& !article.isDeleted()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@GET
	@Path("/getRestaurantOrders{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<OrderListDTO> getRestaurantOrders(@PathParam("id") String id) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		ArrayList<OrderListDTO> orderListDTO = new ArrayList<OrderListDTO>();
		for (Order order : orderDAO.findAll()) {
			if (order.getRestaurant().getId().equals(id)) {
				OrderListDTO dto = new OrderListDTO();
				dto.id = order.getId();
				dto.restaurant = order.getRestaurant();
				dto.dateTimeOrder = order.getDateTimeOrder();
				dto.status = userDAO.convertOrderStatusToString(order.getStatus());
				dto.customerUsername = order.getCustomerUsername();
				dto.delivererUsername = order.getDelivererUsername();

				ArrayList<Article> orderedArticleKeys = new ArrayList<Article>();
				ArrayList<Integer> orderedArticleValues = new ArrayList<Integer>();
				Restaurant restaurant = order.getRestaurant();
				for (Article article : restaurant.getArticles()) {
					for (String orderedArticleName : order.getOrderedArticle().keySet()) {
						if (article.getName().equals(orderedArticleName)) {
							orderedArticleKeys.add(article);
							orderedArticleValues.add(order.getOrderedArticle().get(orderedArticleName));
						}
					}
				}
				dto.price = order.getPrice();
				dto.orderedArticle = orderedArticleKeys;
				dto.numberOfOrder = orderedArticleValues;
				dto.deleted = order.isDeleted();
				orderListDTO.add(dto);
			}
		}
		return orderListDTO;
	}

	@POST
	@Path("/editOrderStatusPrepare{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editOrderStatusPrepare(@PathParam("orderId") String id) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();

		for (Order order : orderDAO.findAll()) {
			if (order.getId().equals(id)) {
				order.setStatus(OrderStatus.IN_PREPARATION);
				orderDAO.saveAll();

				for (User user : userDAO.findAll()) {
					if (user.getCustomerOrders() != null) {
						for (Order o : user.getCustomerOrders()) {
							if (o.getId().equals(id)) {
								o.setStatus(OrderStatus.IN_PREPARATION);
								userDAO.saveAll();
							}
						}
					}
				}
				System.out.println("The task \"edit order status to prepare\" was successfully completed.");
				return Response.status(200).build();
			}
		}

		System.out.println("The task \"edit order status to prepare\" failed.");
		return Response.status(400).entity("The task \"edit order status to prepare\" failed.").build();
	}

	@POST
	@Path("/editOrderStatusWaitDeliverer{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editOrderStatusWaitDeliverer(@PathParam("orderId") String id) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();

		for (Order order : orderDAO.findAll()) {
			if (order.getId().equals(id)) {
				order.setStatus(OrderStatus.ON_HOLD);
				orderDAO.saveAll();

				for (User user : userDAO.findAll()) {
					if (user.getCustomerOrders() != null) {
						for (Order o : user.getCustomerOrders()) {
							if (o.getId().equals(id)) {
								o.setStatus(OrderStatus.ON_HOLD);
								userDAO.saveAll();
							}
						}
					}
				}
				System.out.println("The task \"edit order status tu on hold\" was successfully completed.");
				return Response.status(200).build();
			}
		}

		System.out.println("The task \"edit order status tu on hold\" failed.");
		return Response.status(400).entity("The task \"edit order status tu on hold\" failed.").build();
	}

	@GET
	@Path("/getAllOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<OrderListDTO> getAllOrder() {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		ArrayList<OrderListDTO> orderListDTOS = new ArrayList<OrderListDTO>();
		for (Order order : orderDAO.findAll()) {
			OrderListDTO orderListDTO = new OrderListDTO();
			orderListDTO.id = order.getId();
			orderListDTO.restaurant = order.getRestaurant();
			orderListDTO.dateTimeOrder = order.getDateTimeOrder();
			orderListDTO.status = userDAO.convertOrderStatusToString(order.getStatus());
			orderListDTO.customerUsername = order.getCustomerUsername();
			orderListDTO.delivererUsername = order.getDelivererUsername();

			ArrayList<Article> orderedArticleKeys = new ArrayList<Article>();
			ArrayList<Integer> orderedArticleValues = new ArrayList<Integer>();
			Restaurant restaurant = order.getRestaurant();
			for (Article article : restaurant.getArticles()) {
				for (String orderedArticleName : order.getOrderedArticle().keySet()) {
					if (article.getName().equals(orderedArticleName)) {
						orderedArticleKeys.add(article);
						orderedArticleValues.add(order.getOrderedArticle().get(orderedArticleName));
					}
				}
			}

			orderListDTO.price = order.getPrice();
			orderListDTO.orderedArticle = orderedArticleKeys;
			orderListDTO.numberOfOrder = orderedArticleValues;
			orderListDTO.deleted = order.isDeleted();
			orderListDTOS.add(orderListDTO);
		}
		return orderListDTOS;
	}

	@POST
	@Path("/findOrder{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findOrder(@PathParam("orderId") String id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		User user = (User) request.getSession().getAttribute("user");

		for (Order order : orderDAO.findAll()) {
			if (order.getId().equals(id)) {
				order.setStatus(OrderStatus.REQUEST_SENT);
				order.setCustomerUsername(user.getUsername());
				orderDAO.saveAll();

				for (User user1 : userDAO.findAll()) {
					if (user1.getCustomerOrders() != null) {
						for (Order o : user1.getCustomerOrders()) {
							if (o.getId().equals(id)) {
								o.setStatus(OrderStatus.REQUEST_SENT);
								o.setCustomerUsername(user1.getUsername());
								userDAO.saveAll();
							}
						}
					}
				}
				System.out.println("The task \"find order\" was successfully completed.");
				return Response.status(200).build();
			}
		}

		System.out.println("The task \"find order\" failed.");
		return Response.status(400).entity("The task \"find order\" failed.").build();
	}

	@POST
	@Path("/approveOrder{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response approveOrder(@PathParam("orderId") String id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		String delivererUsername = "";
		boolean flag = false;

		for (Order order : orderDAO.findAll()) {
			if (order.getId().equals(id)) {
				order.setStatus(OrderStatus.SENT);
				delivererUsername = order.getDelivererUsername();
				orderDAO.saveAll();

				for (User user : userDAO.findAll()) {
					if (user.getCustomerOrders() != null) {
						for (Order o : user.getCustomerOrders()) {
							if (o.getId().equals(id)) {
								o.setStatus(OrderStatus.SENT);
								userDAO.saveAll();
								flag = true;
							}
						}
					}
				}
			}
		}

		for (User user : userDAO.findAll()) {
			if (user.getUsername().equals(delivererUsername)) {
				ArrayList<Order> orders = user.getDelivererOrders();
				for (Order order : orderDAO.findAll()) {
					if (order.getId().equals(id)) {
						orders.add(order);
						orderDAO.saveAll();

						if (flag == true) {
							System.out.println("The task \"approve order\" was successfully completed.");
							return Response.status(200).build();
						}
					}
				}
			}
		}

		System.out.println("The task \"approve order\" failed.");
		return Response.status(400).entity("The task \"approve order\" failed.").build();
	}

	@POST
	@Path("/rejectOrder{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response rejectOrder(@PathParam("orderId") String id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();

		for (Order order : orderDAO.findAll()) {
			if (order.getId().equals(id)) {
				order.setStatus(OrderStatus.ON_HOLD);
				order.setDelivererUsername("");
				orderDAO.saveAll();

				for (User user : userDAO.findAll()) {
					if (user.getCustomerOrders() != null) {
						for (Order o : user.getCustomerOrders()) {
							if (o.getId().equals(id)) {
								o.setStatus(OrderStatus.ON_HOLD);
								o.setDelivererUsername("");
								userDAO.saveAll();
								System.out.println("The task \"reject order\" was successfully completed.");
								return Response.status(200).build();
							}
						}
					}
				}
			}
		}

		System.out.println("The task \"reject order\" failed.");
		return Response.status(400).entity("The task \"reject order\" failed.").build();
	}

	@POST
	@Path("/deliverOrders{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deliverOrder(@PathParam("orderId") String id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		boolean flag = false;

		for (Order o : orderDAO.findAll()) {
			if (o.getId().equals(id)) {
				o.setStatus(OrderStatus.DELIVERED);
				orderDAO.saveAll();

				for (User u : userDAO.findAll()) {
					if (u.getCustomerOrders() != null) {
						for (Order or : u.getCustomerOrders()) {
							if (or.getId().equals(id)) {
								or.setStatus(OrderStatus.DELIVERED);
								userDAO.saveAll();
								flag = true;
							}
						}
					}

					if (u.getDelivererOrders() != null) {
						for (Order or : u.getDelivererOrders()) {
							if (or.getId().equals(id)) {
								or.setStatus(OrderStatus.DELIVERED);
								userDAO.saveAll();

								if (flag == true) {
									System.out.println("The task \"deliver order\" was successfully completed.");
									return Response.status(200).build();
								}
							}
						}
					}
				}
			}
		}

		System.out.println("The task \"deliver order\" failed.");
		return Response.status(400).entity("The task \"deliver order\" failed.").build();
	}

	@GET
	@Path("/getDelivererOrders{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<OrderListDTO> getDelivererOrders(@PathParam("username") String username) {
		OrderDAO orderDAO = new OrderDAO();
		UserDAO userDAO = new UserDAO();
		ArrayList<OrderListDTO> dtos = new ArrayList<OrderListDTO>();

		for (Order o : orderDAO.findAll()) {
			if (o.getDelivererUsername() != null && o.getDelivererUsername().equals(username)
					&& (o.getStatus().equals(OrderStatus.DELIVERED) || o.getStatus().equals(OrderStatus.SENT))) {

				OrderListDTO dto = new OrderListDTO();
				dto.id = o.getId();
				dto.restaurant = o.getRestaurant();
				dto.dateTimeOrder = o.getDateTimeOrder();
				dto.status = userDAO.convertOrderStatusToString(o.getStatus());
				dto.customerUsername = o.getCustomerUsername();
				dto.delivererUsername = o.getDelivererUsername();

				ArrayList<Article> orderedArticlesKeys = new ArrayList<Article>();
				ArrayList<Integer> orderedArticlesValues = new ArrayList<Integer>();
				Restaurant restaurant = o.getRestaurant();
				for (Article article : restaurant.getArticles()) {
					for (String orderedArticleName : o.getOrderedArticle().keySet()) {
						if (article.getName().equals(orderedArticleName)) {
							orderedArticlesKeys.add(article);
							orderedArticlesValues.add(o.getOrderedArticle().get(orderedArticleName));
						}
					}
				}
				dto.price = o.getPrice();
				dto.orderedArticle = orderedArticlesKeys;
				dto.numberOfOrder = orderedArticlesValues;
				dto.deleted = o.isDeleted();
				dtos.add(dto);
			}
		}
		return dtos;
	}

	@GET
	@Path("/getRestaurantComments{restaurantId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Comment> getRestaurantComments(@PathParam("restaurantId") String id) {
		CommentDAO commentDAO = new CommentDAO();
		ArrayList<Comment> comments = new ArrayList<Comment>();
		RestaurantDAO restaurantDAO = new RestaurantDAO();

		for (Comment comment : commentDAO.findAll()) {
			if (comment.getRestaurantName().equals(restaurantDAO.findById(id).getName())
					&& comment.isDeleted() == false) {
				comments.add(comment);
			}
		}

		return comments;
	}

	@GET
	@Path("/searchRestaurant/{name}/{location}/{type}/{grade}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Restaurant> searchRestaurant(@PathParam("name") String name,
			@PathParam("location") String location, @PathParam("type") String type, @PathParam("grade") String grade) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		ArrayList<Restaurant> searchedRestaurants = new ArrayList<Restaurant>();

		for (Restaurant r : restaurantDAO.findAllSorted()) {
			if (r.getName().toLowerCase().contains(name.toLowerCase().trim())
					&& r.getLocation().getAdress().toLowerCase().contains(location.toLowerCase().trim())
					&& r.getType().toString().contains(type.trim())) {
				if (r.getAverageGrade() == Double.parseDouble(grade) || grade.equals("0")) {
					searchedRestaurants.add(r);
				}
			}
		}

		return searchedRestaurants;
	}

	@GET
	@Path("/searchMap{location}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Restaurant> pretragaMape(@PathParam("location") String location) {
		System.out.println(location);
		if (location.split(" ")[0].equals("Town")) {
			location = location.substring(4).trim();
		}

		if (location.equals("Novi Sad City")) {
			location = location.substring(0, 8).trim();
		}

		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		ArrayList<Restaurant> searchedRestaurant = new ArrayList<Restaurant>();

		for (Restaurant r : restaurantDAO.findAllSorted()) {
			if (r.getLocation().getAdress().toLowerCase()
					.contains(location.toLowerCase())) {
				searchedRestaurant.add(r);
			}
		}

		return searchedRestaurant;
	}

	@GET
	@Path("/filter/{filterStatus}/{filterType}/{name}/{location}/{type}/{grade}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Restaurant> filter(@PathParam("filterStatus") String filterStatus,
			@PathParam("filterType") String filterType, @PathParam("name") String name,
			@PathParam("location") String location, @PathParam("type") String type, @PathParam("grade") String grade) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		ArrayList<Restaurant> searchedRestaurants = new ArrayList<Restaurant>();

		for (Restaurant r : restaurantDAO.findAllSorted()) {
			if (r.getName().toLowerCase().contains(name.toLowerCase().trim())
					&& r.getLocation().getAdress().toLowerCase().contains(location.toLowerCase().trim())
					&& r.getType().toString().contains(type.trim())) {
				if (r.getAverageGrade() == Double.parseDouble(grade) || grade.equals("0")) {
					if ((r.getStatus().toString().equals(filterStatus) || filterStatus.equals(" "))
							&& (r.getType().toString().equals(filterType) || filterType.equals(" "))) {
						searchedRestaurants.add(r);
					}
				}
			}
		}

		return searchedRestaurants;
	}
	Comparator<Restaurant> compareByRestaurantName = new Comparator<Restaurant>() {
	    @Override
	    public int compare(Restaurant r1, Restaurant r2) {
	        return r1.getName().compareTo(r2.getName());
	    }
	};
	
	Comparator<Restaurant> compareByAdress = new Comparator<Restaurant>() {
	    @Override
	    public int compare(Restaurant r1, Restaurant r2) {
	        return r1.getLocation().getAdress().compareTo(r2.getLocation().getAdress());
	    }
	};
	
	Comparator<Restaurant> compareByAverageGrade = new Comparator<Restaurant>() {
	    @Override
	    public int compare(Restaurant r1, Restaurant r2) {
	    	return Double.compare(r1.getAverageGrade(), r2.getAverageGrade());
	    }
	};
	
	
	@GET
	@Path("/descendingSort/{number}/{filterStatus}/{filterType}/{name}/{location}/{type}/{grade}")
	@Produces(MediaType.APPLICATION_JSON)  
	public ArrayList<Restaurant> descendingSort(@PathParam("number") int number, @PathParam("filterStatus") String filterStatus, 
			@PathParam("filterType") String filterType, @PathParam("name") String name, @PathParam("location") String location,
			@PathParam("type") String type, @PathParam("grade") String grade)
	{
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		ArrayList<Restaurant> sortedRestaurants = new ArrayList<Restaurant>();		
						
		for (Restaurant r: restaurantDAO.findAllSorted())
		{
			if(	r.getName().toLowerCase().contains(name.toLowerCase().trim()) && 
					r.getLocation().getAdress().toLowerCase().contains(location.toLowerCase().trim())  && 
						r.getType().toString().contains(type.trim())  )
			{
				if ( r.getAverageGrade() == Double.parseDouble(grade) || grade.equals("0") ) 
				{
					if(	(r.getStatus().toString().equals(filterStatus) || filterStatus.equals(" ")) &&  
						    (r.getType().toString().equals(filterType) ||  filterType.equals(" "))  )
						{
							sortedRestaurants.add(r);
						}
				}
			}
		}
		
		if (number == 1)
		{
			Collections.sort(sortedRestaurants, compareByRestaurantName.reversed());
		}
		else if (number == 2)
		{
			Collections.sort(sortedRestaurants, compareByAdress.reversed());
		}
		else if (number == 3)
		{
			Collections.sort(sortedRestaurants,compareByAverageGrade.reversed());
		}
				
		return sortedRestaurants;
	}
	
	@GET
	@Path("/ascendingSort/{number}/{filterStatus}/{filterType}/{name}/{location}/{type}/{grade}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Restaurant> ascendingSort(@PathParam("number") int number, @PathParam("filterStatus") String filterStatus, 
			@PathParam("filterType") String filterType, @PathParam("name") String name, @PathParam("location") String location,
			@PathParam("type") String type, @PathParam("grade") String grade) 
	{
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		ArrayList<Restaurant> sortedRestaurants = new ArrayList<Restaurant>();		
				
		for (Restaurant r: restaurantDAO.findAllSorted())
		{
			if(	r.getName().toLowerCase().contains(name.toLowerCase().trim()) && 
					r.getLocation().getAdress().toLowerCase().contains(location.toLowerCase().trim())  && 
						r.getType().toString().contains(type.trim())  )
			{
				if ( r.getAverageGrade() == Double.parseDouble(grade) || grade.equals("0") ) 
				{
					if(	(r.getStatus().toString().equals(filterStatus) || filterStatus.equals(" ")) &&  
						    (r.getType().toString().equals(filterType) ||  filterType.equals(" "))  )
						{
							sortedRestaurants.add(r);
						}
				}
			}
		}
	
		if (number == 1)
		{
			Collections.sort(sortedRestaurants, compareByRestaurantName);
		}
		else if (number == 2)
		{
			Collections.sort(sortedRestaurants, compareByAdress);
		}
		else if (number == 3)
		{
			Collections.sort(sortedRestaurants,compareByAverageGrade);
		}
		
		return sortedRestaurants;
	}

	/*
	 * @GET
	 * 
	 * @Path(
	 * "/sortAscenindgOrder/{id}/{minPrice}/{maxPrice}/{dateStart}/{dateEnd}/{filterStatus}/{number}/{sortType}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public ArrayList<Order>
	 * sortAscendingOrder(@PathParam("id") String restaurantId,
	 * 
	 * @PathParam("minPrice") String minPrice, @PathParam("maxPrice") String
	 * maxPrice,
	 * 
	 * @PathParam("dateStart") String dateStart, @PathParam("dateEnd") String
	 * dateEnd,
	 * 
	 * @PathParam("filterStatus") String filterStatus, @PathParam("number") int
	 * number,
	 * 
	 * @PathParam("sortType") int sortType) { OrderDAO orderDAO = new OrderDAO();
	 * ArrayList<Order> searchedOrder = new ArrayList<Order>();
	 * 
	 * if (number == 1) { Date start = new Date(); Date end = new Date();
	 * 
	 * try { start = new SimpleDateFormat("yyyy-MM-dd").parse(dateStart); } catch
	 * (ParseException e) { e.printStackTrace(); }
	 * 
	 * try { end = new SimpleDateFormat("yyyy-MM-dd").parse(dateEnd); } catch
	 * (ParseException e) { e.printStackTrace(); }
	 * 
	 * for (Order order : orderDAO.findAll()) { if
	 * (order.getRestaurant().getId().equals(restaurantId)) { try { String dateOrder
	 * = order.getDateTimeOrder().split(" ")[0]; String[] data =
	 * dateOrder.split("-"); // dd-MM-yyyy String newDate = data[2] + "-" + data[1]
	 * + "-" + data[0]; Date nightsDate = new
	 * SimpleDateFormat("yyyy-MM-dd").parse(newDate);
	 * 
	 * if (((nightsDate.after(start) || nightsDate.getDate() ==
	 * nightsDate.getDate()) && (nightsDate.before(end) || nightsDate.getDate() ==
	 * end.getDate())) || (dateStart.equals(" ") || dateEnd.equals(" "))) { if
	 * (order.getStatus().toString().equals(filterStatus) ||
	 * filterStatus.equals(" ")) { searchedOrder.add(order); } }
	 * 
	 * } catch (ParseException e) { e.printStackTrace(); } } } } else if (number ==
	 * 2) { for (Order order : orderDAO.findAll()) { if
	 * (order.getRestaurant().getId().equals(restaurantId) && order.getPrice() >=
	 * Double.parseDouble(minPrice) && order.getPrice() <=
	 * Double.parseDouble(maxPrice)) { if
	 * (order.getStatus().toString().equals(filterStatus) ||
	 * filterStatus.equals(" ")) { searchedOrder.add(order); } } } } else if (number
	 * == 0) { for (Order order : orderDAO.findAll()) { if
	 * (order.getRestaurant().getId().equals(restaurantId)) { if
	 * (order.getStatus().toString().equals(filterStatus) ||
	 * filterStatus.equals(" ")) { searchedOrder.add(order); } } } }
	 * 
	 * if (sortType == 1) { Collections.sort(searchedOrder, compareByDate); } else
	 * if (sortType == 2) { Collections.sort(searchedOrder, compareByPrice); }
	 * 
	 * return searchedOrder; }
	 * 
	 * /*Comparator<Porudzbina> compareByDatum = new Comparator<Porudzbina>() { //
	 * sortiranje po datumu
	 * 
	 * @Override public int compare(Porudzbina o1, Porudzbina o2) {
	 * 
	 * String datumPorudzbine = o1.getDatumVremePorudzbine().split(" ")[0]; String
	 * [] podaci = datumPorudzbine.split("-"); // dd-MM-yyyy String noviDatum =
	 * podaci[2] + "-" + podaci[1] + "-" + podaci[0]; Date konaciDatum = new Date();
	 * try { konaciDatum = new SimpleDateFormat("yyyy-MM-dd").parse(noviDatum); }
	 * catch (ParseException e) { e.printStackTrace(); }
	 * 
	 * String datumPorudzbine2 = o2.getDatumVremePorudzbine().split(" ")[0]; String
	 * [] podaci2 = datumPorudzbine2.split("-"); // dd-MM-yyyy String noviDatum2 =
	 * podaci2[2] + "-" + podaci2[1] + "-" + podaci2[0]; Date konaciDatum2 = new
	 * Date(); try { konaciDatum2 = new
	 * SimpleDateFormat("yyyy-MM-dd").parse(noviDatum2); } catch (ParseException e)
	 * { e.printStackTrace(); }
	 * 
	 * return konaciDatum.compareTo(konaciDatum2); } };
	 * 
	 * Comparator<Order> compareByPrice = new Comparator<Order>() {
	 * 
	 * @Override public int compare(Porudzbina o1, Porudzbina o2) { return (int)
	 * (o1.getCena() - o2.getCena()); }
	 * 
	 * @Override public int compare(Order o1, Order o2) { // TODO Auto-generated
	 * method stub return 0; } };
	 */

	@DELETE
	@Path("/deleteArtikcle/{id}/{articleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArticle(@PathParam("articleId") String articleId, @PathParam("id") String id) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		UserDAO userDAO = new UserDAO();

		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(id)) {
				for (Article article : restaurant.getArticles()) {
					if (article.getId().equals(articleId)) {
						article.setDeleted(true);
						restaurantDAO.saveAll();
					}
				}

				for (User user : userDAO.findAll()) {
					if (user.getRestaurant() != null && user.getRestaurant().getId().equals(id)) {
						user.setRestaurant(restaurant);
						userDAO.saveAll();
					}
				}
				System.out.println("The task \"delete article by id from restaurant\" was successfully completed.");
				return Response.status(200).build();
			}
		}
		System.out.println("The task \"delete article by id from restaurant\" failed.");
		return Response.status(400).entity("The task \"delete article by id from restaurant\" failed.").build();
	}

	@DELETE
	@Path("/deleteRestaurant{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRestaurant(@PathParam("id") String id) {
		RestaurantDAO restaurantDAO = (RestaurantDAO) context.getAttribute("restaurant");
		UserDAO userDAO = new UserDAO();

		for (User user : userDAO.findAll()) {
			if (user.getRestaurant() != null) {
				if (user.getRestaurant().getId().equals(id)) {
					user.setRestaurant(null);
					userDAO.saveAll();
				}
			}
		}

		for (Restaurant restaurant : restaurantDAO.findAll()) {
			if (restaurant.getId().equals(id)) {
				restaurant.setDeleted(true);
				restaurantDAO.saveAll();
				System.out.println("The task \"delete restaurant\" was successfully completed.");
				return Response.status(200).build();
			}
		}
		System.out.println("The task \"delete restaurant\" failed.");
		return Response.status(400).entity("The task \"delete restaurant\" failed.").build();
	}

	@POST
	@Path("/addImage")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addImage() {
		System.out.println("The task \"add image\" was successfully completed.");
	}

}
