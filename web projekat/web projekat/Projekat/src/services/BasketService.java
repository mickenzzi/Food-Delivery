package services;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.entities.Restaurant;
import beans.entities.User;
import dao.UserDAO;
import dao.OrderDAO;
import dao.RestaurantDAO;
import dto.ArticleDTO;
import dto.BasketDTO;

@Path("/baskets")
public class BasketService {

	@Context
	ServletContext context;

	@POST
	@Path("/addArticle")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addArticle(@Context HttpServletRequest request, ArticleDTO a) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return Response.status(400).entity("User not logged in.").build();
		}
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		Restaurant restaurant = restaurantDAO.findById(a.restaurantId);
		user.setRestaurant(restaurant);
		UserDAO userDAO = new UserDAO();
		userDAO.addArticleBasket(user, a);
		System.out.println("The task \"add article\" was successfully completed.");
		return Response.status(200).build();
	}

	@DELETE
	@Path("/deleteArticle/{articleName}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteArticle(@PathParam("articleName") String articleName, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return;
		}
		UserDAO userDAO = new UserDAO();
		User newUser = userDAO.deleteBasketArticle(user, articleName);
		if (newUser == null) {
			System.out.println("New user is null.");
			return;
		}
		request.getSession().setAttribute("user", newUser);
		System.out.println("The task \"delete article\" was successfully completed.");
	}

	@GET
	@Path("/getBasket")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BasketDTO> getBasket(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return null;
		}
		UserDAO userDAO = new UserDAO();
		System.out.println("The task \"get basket\" was successfully completed.");
		return userDAO.getBasketDTO(user);
	}

	@DELETE
	@Path("/deleteBasket")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteBasket(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return;
		}
		UserDAO userDAO = new UserDAO();
		User newUser = userDAO.deleteBasket(user);
		if (newUser == null) {
			System.out.println("New user is null.");
			return;
		}
		request.getSession().setAttribute("user", newUser);
		System.out.println("The task \"delete basket\" was successfully completed.");
	}

	@GET
	@Path("/addOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrder(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return Response.status(400).entity("User not logged in.").build();
		}
		UserDAO userDAO = new UserDAO();
		User newUser = userDAO.addOrder(user);
		if (newUser == null) {
			System.out.println("New user is null.");
			return Response.status(400).entity("New user is null.").build();
		}
		request.getSession().setAttribute("user", newUser);
		System.out.println("The task \"add order\" was successfully completed.");
		return Response.status(200).build();
	}

	@DELETE
	@Path("/deleteOrder{idOrder}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteOrder(@PathParam("idOrder") String idOrder, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return;
		}
		UserDAO userDAO = new UserDAO();
		User newUser = userDAO.deleteOrder(user, idOrder);
		if (newUser == null) {
			System.out.println("New user is null.");
			return;
		}

		OrderDAO orderDAO = new OrderDAO();
		if (!orderDAO.delete(idOrder)) {
			System.out.println("The task \"delete order\" failed");
			return;
		}
		request.getSession().setAttribute("user", newUser);
		System.out.println("The task \"delete order\" was successfully completed.");
	}
}
