package services;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.entities.Comment;
import beans.enums.CommentStatus;
import beans.entities.User;
import beans.entities.Restaurant;
import dao.CommentDAO;
import dao.RestaurantDAO;
import dto.CommentDTO;

@Path("/comments")
public class CommentService {
	@Context
	ServletContext context;

	@POST
	@Path("/addComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComment(@Context HttpServletRequest request, CommentDTO comment) {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("User not logged in.");
			return Response.status(400).entity("User not logged in.").build();
		}
		CommentDAO commentDAO = new CommentDAO();
		commentDAO.add(comment, user);
		System.out.println("The task \"add comment\" was successfully completed.");
		return Response.status(200).build();
	}

	@DELETE
	@Path("/deleteComment{idComment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComment(@PathParam("idComment") int id) {
		CommentDAO commentDAO = new CommentDAO();
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		int numOfGrade = 0;
		double grade = 0;
		String restaurantName = "";

		for (Comment c : commentDAO.findAll()) {
			if (c.getId() == id) {
				c.setDeleted(true);
				restaurantName = c.getRestaurantName();
				commentDAO.saveAll();
			}
		}
		// After logic deleting,we need to reorganize avg grade of restaurant
		for (Comment c : commentDAO.findAll()) {
			if (c.getStatus().equals(CommentStatus.APPROVED) && c.getRestaurantName().equals(restaurantName)
					&& c.isDeleted() == false) {
				grade = grade + c.getGrade();
				numOfGrade = numOfGrade + 1;
			}
		}
		// we use status(200) for succes operations
		// we use status(400) for bad request
		double avgGrade = grade / numOfGrade;
		for (Restaurant r : restaurantDAO.findAll()) {
			if (r.getName().equals(restaurantName)) {
				r.setAverageGrade(Math.round(avgGrade));
				restaurantDAO.saveAll();
				System.out.println("The task \"delete comment\" was successfully completed.");
				return Response.status(200).build();
			}
		}
		System.out.println("The task \"delete comment\" failed.");
		return Response.status(400).entity("The task \"delete comment\" failed.").build();
	}

	@POST
	@Path("/rejectComment{idComment}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response rejectComment(@PathParam("idComment") int id) {
		CommentDAO commentDAO = new CommentDAO();

		for (Comment c : commentDAO.findAll()) {
			if (c.getId() == id) {
				c.setStatus(CommentStatus.REJECTED);
				c.setApproved(false);
				commentDAO.saveAll();
				System.out.println("The task \"comment rejection\" was successfully completed.");
				return Response.status(200).build();
			}
		}

		System.out.println("The task \"comment rejection\" failed!");
		return Response.status(400).entity("The task \"comment rejection\" failed!").build();
	}

	@POST
	@Path("/approveComment{idComment}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response approveComment(@PathParam("idComment") int id) {
		CommentDAO commentDAO = new CommentDAO();
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		int numOfGrade = 0;
		double grade = 0;
		String restaurantName = "";

		for (Comment c : commentDAO.findAll()) {
			if (c.getId() == id) {
				restaurantName = c.getRestaurantName();
				c.setStatus(CommentStatus.APPROVED);
				c.setApproved(true);
				commentDAO.saveAll();
			}
		}

		for (Comment c : commentDAO.findAll()) {
			if (c.getStatus().equals(CommentStatus.APPROVED) && c.getRestaurantName().equals(restaurantName)
					&& c.isDeleted() == false) {
				grade = grade + c.getGrade();
				numOfGrade = numOfGrade + 1;
			}
		}

		double avgGrade = grade / numOfGrade;

		for (Restaurant r : restaurantDAO.findAll()) {
			if (r.getName().equals(restaurantName)) {
				r.setAverageGrade(Math.round(avgGrade));
				restaurantDAO.saveAll();
				System.out.println("The task \"approve comment\" was successfully completed.");
				return Response.status(200).build();
			}
		}

		System.out.println("The task \"approve comment\" failed.");
		return Response.status(400).entity("The task \"approve comment\failed.").build();
	}

}
