package com.watermelonlabs.contacts44.db;

import com.watermelonlabs.contacts44.model.User;

public interface UserDao {

	public User findUser(String userid);

	public boolean saveUser(User user);

	public boolean updateUser(User user);

	public boolean deleteUser(User user);

}
