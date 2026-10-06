package com.safebank.banking.service;

import java.util.List;

import com.safebank.banking.entity.User;

public interface UserService {

	User saveUser(User user);
	List<User>getAllUsers();
	User getUserById(Long id);
	User updateUser(Long id, User user);
	User approveUser(Long id);
	User rejectUser(Long id);
	User getUserByMobile(String mobile);
	User deleteUser(Long id);

	User getUserByEmail(String email);

    User getUserByAccountNumber(String accountNumber);
   
    User changePin(Long id,String currentPin,String newPin);
   
}
