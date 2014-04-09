package com.watermelonlabs.contacts44.db;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.watermelonlabs.contacts44.model.Contact;
import com.watermelonlabs.contacts44.util.TagTokenizer;

public class ContactDaoMonogo implements ContactDao{

	@Override
	public boolean saveContact(Contact contact) {
		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("contacts44_contacts");
		//We will do this op while processing to save on storage cost
		//contact.setTags(TagTokenizer.tokenize(contact.getContact()));
		contact.setContactid(MongoUtil.getnextUniqueId("ContactId"));
		BasicDBObject doc = createUserDocument(contact);
		coll.insert(doc);

		//TODO
		//Create index, not go live critical
		
		return true;
	}

	private BasicDBObject createUserDocument(Contact contact) {	
		return new BasicDBObject("contactid", contact.getContactid()).
                append("userid", contact.getUserid()).
                append("contact", contact.getContact()).
                append("tags", contact.getTags());
	}

	@Override
	public boolean deleteContact(Contact contact) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Contact findContact(long contactid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> searchContacts(List<String> tags, String scope, String userid) {
		List<Contact> matchingContactsList = new ArrayList();
		if(scope.equals("local")){
			List<Contact> allUserContacts = findContactsbyUser(userid);
			//Iterate the list and produce tokens
			//Compare match with searched tokens, if match add to result.
			for(Contact c: allUserContacts){
				List<String> cTokens = TagTokenizer.tokenize(c.getContact());
				if(cTokens.containsAll(tags)){
					matchingContactsList.add(c);
				}
			}
		}
		return matchingContactsList;
	}
	
	private List<Contact> findContactsbyUser(String userId) {
		DB db = MongoConnector.connect();
		DBCollection coll = db.getCollection("contacts44_contacts");		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("userid", userId);	 
		DBCursor cursor = coll.find(searchQuery);
		List<Contact> allUserContacts = new ArrayList<Contact>();
		while (cursor.hasNext()) {
			BasicDBObject doc = (BasicDBObject) cursor.next();
			Contact contact = new Contact();
			contact.setContact(doc.getString("contact"));
			contact.setContactid(doc.getLong("contactid"));
			contact.setUserid(doc.getString("userid"));
			allUserContacts.add(contact);
		}		
		return allUserContacts;
	}

}
