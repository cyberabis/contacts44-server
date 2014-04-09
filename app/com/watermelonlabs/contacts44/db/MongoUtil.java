package com.watermelonlabs.contacts44.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoUtil {

	public static long getnextUniqueId(String counterName) {

		long nextUniqueId = 0;

		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("nextSequence");

		DBObject modifier = new BasicDBObject("value", 1);
		DBObject incQuery = new BasicDBObject("$inc", modifier);
		DBObject searchQuery = new BasicDBObject("name", counterName);
		BasicDBObject newDoc = (BasicDBObject) coll.findAndModify(searchQuery, null, null, false, incQuery, true, true);
		if (newDoc != null){
			nextUniqueId = newDoc.getInt("value");
		}

		return nextUniqueId;
	}

}
