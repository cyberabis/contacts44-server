package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.watermelonlabs.contacts44.db.UserDao;
import com.watermelonlabs.contacts44.db.UserDaoMongo;
import com.watermelonlabs.contacts44.model.User;

import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result connectionTest() {
		String sessionUser = session("connected");
		ObjectNode result = Json.newObject();
		result.put("user", sessionUser);
		return ok(result);
	}

	/**
	 * Needs json request with attributes: userid, password (mandatory) signup,
	 * email (optional) If successful will return OK with json msg as loggedin
	 * or connected.
	 * 
	 * @return Result
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result login() {

		JsonNode req = request().body().asJson();

		if (req == null) {
			return badRequest("Expecting Json data");
		} else {

			String userid = req.findPath("userid").textValue();
			String password = req.findPath("password").textValue();
			ObjectNode result = Json.newObject();
			
			//Input data validations
			if((userid==null)||(password==null)){
				return unauthorized("userid or password blank");
			}

			// Check if user already logged in
			String sessionUser = session("connected");
			if ((sessionUser != null) && (sessionUser.equals(userid))) {
				result.put("msg", "connected");
				return ok(result);
			} else {
				session().clear();
				
				// Authenticate
				UserDao userDao = new UserDaoMongo();
				User user = userDao.findUser(userid);
				if (user == null) {
					// Check if needs to be signed up
					String signup = req.findPath("signup").textValue();
					if ((signup!= null)&&(signup.equals("yes"))) {
						// Create user
						String emailId = req.findPath("email").textValue();
						user = new User();
						user.setUserId(userid);
						user.setPassword(password);
						user.setEmailId(emailId);
						boolean isUserCreated = userDao.saveUser(user);
						// Set cookie
						if (isUserCreated) {
							session("connected", userid);
							result.put("msg", "loggedin");
							return ok(result);
						} else
							return unauthorized("could not create user");
					} else {
						return unauthorized("userid incorrect");
					}
				} else {
					if ((user.getUserId().equals(userid))
							&& (user.getPassword().equals(password))) {
						// Set cookie
						session("connected", userid);
						result.put("msg", "loggedin");
						return ok(result);
					} else {
						return unauthorized("password incorrect");
					}
				}
			}
		}
	}
	

	public static Result logout() {
		session().clear();
		return ok();
	}

}
