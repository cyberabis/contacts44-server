package com.watermelonlabs.contacts44.db;

import com.mongodb.*;

import com.watermelonlabs.contacts44.model.User;

public class UserDaoMongo implements UserDao {

	@Override
	public User findUser(String userid) {		
		User user = null;
		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("contacts44_users");		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("userid", userid);	 
		DBCursor cursor = coll.find(searchQuery);

		if (cursor.hasNext()) {
			BasicDBObject doc = (BasicDBObject) cursor.next();
			user = new User();
			user.setUserId(doc.getString("userid"));
			user.setPassword(doc.getString("password"));
			user.setEmailId(doc.getString("email"));
			user.setTags(doc.getString("tags"));
		}		
		return user;
	}

	@Override
	public boolean saveUser(User user) {		
		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("contacts44_users");
		BasicDBObject doc = createUserDocument(user);
		coll.insert(doc);
		return true;
	}

	@Override
	public boolean updateUser(User user) {
		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("contacts44_users");
		BasicDBObject doc = createUserDocument(user);
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("userid", user.getUserId());	 
		coll.update(searchQuery, doc);
		return true;
	}

	@Override
	public boolean deleteUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	private BasicDBObject createUserDocument(User user) {
		return new BasicDBObject("userid", user.getUserId()).
                append("password", user.getPassword()).
                append("tags", user.getTags()).
                append("email", user.getEmailId());
	}

}
