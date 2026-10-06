
package com.safebank.banking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safebank.banking.entity.User;
import com.safebank.banking.security.JwtService;
import com.safebank.banking.service.UserService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // ==============================
    // ADMIN LOGIN
    // ==============================

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        // Admin credentials
        if ("admin@safebank.com".equals(email)
                && "admin123".equals(password)) {

            String token =
                    jwtService.generateToken(email, "ADMIN");

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Admin login successful",
                            "token", token,
                            "role", "ADMIN"
                    )
            );
        }

        return ResponseEntity
                .status(401)
                .body(
                    Map.of(
                        "message",
                        "Invalid admin email or password"
                    )
                );
    }

    // ==============================
    // GET USER / APPLICATION
    // ==============================

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable Long id) {

        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(user);
    }

    // ==============================
    // APPROVE APPLICATION
    // ==============================

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(
            @PathVariable Long id) {

        try {

            User user = userService.approveUser(id);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ==============================
    // REJECT APPLICATION
    // ==============================

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectUser(
            @PathVariable Long id) {

        try {

            User user = userService.rejectUser(id);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ==============================
    // UPDATE USER
    // ==============================

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        try {

            User updatedUser =
                    userService.updateUser(id, user);

            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ==============================
    // DELETE USER
    // ==============================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {

        try {

            User deletedUser =
                    userService.deleteUser(id);

            return ResponseEntity.ok(deletedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}

