package com.watermelonlabs.contacts44.db;

import java.net.UnknownHostException;

import  com.mongodb.*;

public class MongoConnector {

	public static DB connect() {

		DB db = null;
		try {
			Mongo mongo = new Mongo("ds037478.mongolab.com",37478);
			db = mongo.getDB("trc");
			db.authenticate("trcuser", "trcpass".toCharArray());
		} catch (UnknownHostException e) {
			e.printStackTrace();
	    } catch (MongoException e) {
		e.printStackTrace();
	    }
		return db;
	}

}
