package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.watermelonlabs.contacts44.db.ContactDao;
import com.watermelonlabs.contacts44.db.ContactDaoMonogo;
import com.watermelonlabs.contacts44.db.UserDao;
import com.watermelonlabs.contacts44.db.UserDaoMongo;
import com.watermelonlabs.contacts44.model.Contact;
import com.watermelonlabs.contacts44.model.User;
import com.watermelonlabs.contacts44.util.TagTokenizer;

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
				result.put("msg", "already connected");
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
							result.put("userid", user.getUserId());
							result.put("password", user.getPassword());
							result.put("email", user.getEmailId());
							result.put("tags", user.getTags());
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
						result.put("userid", user.getUserId());
						result.put("password", user.getPassword());
						result.put("email", user.getEmailId());
						result.put("tags", user.getTags());
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
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result add() {
		
		String sessionUser = session("connected");
		if(sessionUser==null){
			return badRequest("Not logged in");
		}

		JsonNode req = request().body().asJson();
		if (req == null) {
			return badRequest("Expecting Json data");
		} else {
			String contact = req.findPath("contact").textValue();
			if ((contact == null) || (contact.equals(""))){
				return ok("Nothing to save");
			}
			else{
				//Save the contact
				Contact contactObj = new Contact();
				contactObj.setContact(contact);
				contactObj.setUserid(sessionUser);
				ContactDao contactDao = new ContactDaoMonogo();
				contactDao.saveContact(contactObj);
				return ok("Saved");
			}
			
		}
	}
	
	
	public static Result search(String tags, String scope) {
		
		String sessionUser = session("connected");
		if(sessionUser==null){
			return badRequest("Not logged in");
		}
		
		ArrayNode result = null;
		if ((tags!=null)&&(!tags.equals(""))){
			ContactDao contactDao = new ContactDaoMonogo();
			List<String> tagList = TagTokenizer.tokenize(tags);
			List<Contact> contacts = contactDao.searchContacts(tagList, scope, sessionUser);
			ObjectMapper mapper = new ObjectMapper(); 
			result = mapper.valueToTree(contacts);
		}
		return ok(result);
		
	}
	
	
	public static Result myProfile(){
		String sessionUser = session("connected");
		if(sessionUser==null){
			return badRequest("Not logged in");
		}
		else {
			ObjectNode result = Json.newObject();
			UserDao userDao = new UserDaoMongo();
			User user = userDao.findUser(sessionUser);
			result.put("userid", user.getUserId());
			result.put("email", user.getEmailId());
			result.put("tags", user.getTags());
			result.put("password", user.getPassword());
			return ok(result);
		}
	
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateProfile(){
		String sessionUser = session("connected");
		if(sessionUser==null){
			return badRequest("Not logged in");
		}
		else {
			JsonNode req = request().body().asJson();
			if (req == null) {
				return badRequest("Expecting Json data");
			} else {
				String email = req.findPath("email").textValue();
				String tags = req.findPath("tags").textValue();
				String password = req.findPath("password").textValue();
				User user = new User();
				user.setUserId(sessionUser);
				user.setTags(tags);
				user.setEmailId(email);
				user.setPassword(password);
				UserDao userDao = new UserDaoMongo();
				userDao.updateUser(user);
				return ok();
			}
		}
	}
	
}
