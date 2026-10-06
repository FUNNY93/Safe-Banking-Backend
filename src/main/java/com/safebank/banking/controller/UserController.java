package com.safebank.banking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safebank.banking.entity.User;
import com.safebank.banking.service.UserService;


@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
	
	@Autowired
	private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


	

    // Create User
    @PostMapping
    public User saveUser(@RequestBody User user) {
    	System.out.println("post/users comtrolled reached");
        return userService.saveUser(user);
    }

    // Get All Users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get User By Id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                           @RequestBody User user) {
    		System.out.println("PUT API CALLED");
        return userService.updateUser(id, user);
    }
    
    @PutMapping("/{id}/approve")
    public User approve(@PathVariable Long id) {

        return userService.approveUser(id);
    }

    @PutMapping("/{id}/reject")
    public User reject(@PathVariable Long id) {

        return userService.rejectUser(id);
    }
    
    @PutMapping("/{id}/pin")
    public ResponseEntity<?> changePin(
            @PathVariable Long id,
            @RequestBody Map<String, String> data) {

        try {

            String currentPin =
                    data.get("currentPin");

            String newPin =
                    data.get("newPin");

            User updatedUser =
                    userService.changePin(
                            id,
                            currentPin,
                            newPin
                    );

            return ResponseEntity.ok(
                    updatedUser
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
 // Delete User
    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable Long id) {

        
        return userService.deleteUser(id);

       
    }
    
    @GetMapping("/mobile/{mobile}")
    public User getUserByMobile(@PathVariable String mobile) {
        return userService.getUserByMobile(mobile);
    }
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}
