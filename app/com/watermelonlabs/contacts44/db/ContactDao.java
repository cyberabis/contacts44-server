package com.watermelonlabs.contacts44.db;

import java.util.List;

import com.watermelonlabs.contacts44.model.Contact;

public interface ContactDao {
	
	public boolean saveContact(Contact contact);
	public boolean deleteContact(Contact contact);
	public Contact findContact(long contactid);
	public List<Contact> searchContacts(List<String> tags, String scope, String userid);
	
}
