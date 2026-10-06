package com.safebank.banking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safebank.banking.entity.User;
import com.safebank.banking.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	//UserRepository userRepository = new UserRepository();
	
	//save user
	@Override
	public User saveUser(User user) {
		// Duplicate Mobile
	    if (userRepository.existsByMobile(user.getMobile())) {
	        throw new RuntimeException("Mobile Number already exists");
	    }

	    // Duplicate Email
	    if (userRepository.existsByEmail(user.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    // Duplicate aadhar
	    if (userRepository.existsByAadhar(user.getAadhar())) {
	        throw new RuntimeException("Aadhaar Number already exists");
	    }

	    // Duplicate PAN
	    if (userRepository.existsByPan(user.getPan())) {
	        throw new RuntimeException("PAN Number already exists");
	    }
		 // Generate Application Number
	    String applicationNumber = "SB" + System.currentTimeMillis();

	    user.setApplicationNumber(applicationNumber);

	    // Default Status
	    user.setStatus("Pending");

	    return userRepository.save(user);
		  
		
	}
	 // Get All Users
	@Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Get User By Id
	@Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Delete User
	@Override
	public User deleteUser(Long id) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException("User not found"));

	    userRepository.delete(user);

	    return user;
	}
   
	@Override
	public User updateUser(Long id, User updatedUser) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    if (updatedUser.getMobile() != null) {
	        user.setMobile(updatedUser.getMobile());
	    }

	    if (updatedUser.getEmail() != null) {
	        user.setEmail(updatedUser.getEmail());
	    }

	    if (updatedUser.getAddress() != null) {
	        user.setAddress(updatedUser.getAddress());
	    }

	
	 

	    return userRepository.save(user);
	}
	
	@Override
	public User approveUser(Long id) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setStatus("Approved");

	    user.setAccountNumber(
	            "4567" + (10000000 + new java.util.Random().nextInt(90000000))
	    );
	    
	    // Generate IFSC code
	    user.setIfscCode(generateIfscCode());


	    return userRepository.save(user);
	}

	@Override
	public User rejectUser(Long id) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setStatus("Rejected");

	    return userRepository.save(user);
	}
	@Override
	public User getUserByMobile(String mobile) {
	    return userRepository.findByMobile(mobile);
	}
	@Override
	public User getUserByEmail(String email) {
	    return userRepository.findByEmail(email);
	}
	@Override
	public User getUserByAccountNumber(String accountNumber) {

	    return userRepository.findByAccountNumber(
	            accountNumber
	    );
	}
	 public User changePin(
	            Long id,
	            String currentPin,
	            String newPin) {

	        User user =
	                userRepository.findById(id)
	                        .orElseThrow(
	                            () -> new RuntimeException(
	                                "User not found"
	                            )
	                        );


	        // Check current PIN
	        if (
	            !user.getPin()
	                 .equals(currentPin)
	        ) {

	            throw new RuntimeException(
	                "Current PIN is incorrect"
	            );
	        }


	        // Validate new PIN
	        if (
	            newPin == null ||
	            !newPin.matches("\\d{4}")
	        ) {

	            throw new RuntimeException(
	                "PIN must contain exactly 4 digits"
	            );
	        }


	        // Update database
	        user.setPin(newPin);


	        return userRepository.save(user);
	    }
	    private String generateIfscCode() {

	        String bankCode = "SAFE";

	        String reservedCharacter = "0";

	        String branchCode = "001234";

	        return bankCode + reservedCharacter + branchCode;
	    }
	   
}
