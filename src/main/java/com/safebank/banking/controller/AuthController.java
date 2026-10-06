package com.safebank.banking.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.safebank.banking.entity.User;
import com.safebank.banking.service.EmailService;
import com.safebank.banking.service.UserService;
import com.safebank.banking.security.JwtService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtService jwtService;

    private final Map<String, String> otpStorage =
            new HashMap<>();


    // =========================
    // SEND OTP
    // =========================

    @PostMapping("/send-otp")
    public Map<String, String> sendOtp(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        Map<String, String> response =
                new HashMap<>();

        if (email == null || email.isBlank()) {

            response.put(
                    "message",
                    "Email is required"
            );

            return response;
        }


        // Find user using email

        User user =
                userService.getUserByEmail(email);


        if (user == null) {

            response.put(
                    "message",
                    "User not found"
            );

            return response;
        }


        // Check approval

        if (!"Approved".equalsIgnoreCase(
                user.getStatus())) {

            response.put(
                    "message",
                    "Your account is not approved yet"
            );

            return response;
        }


        // Generate 6 digit OTP

        String otp =
                String.valueOf(
                        100000 +
                        new Random().nextInt(900000)
                );


        // Store OTP

        otpStorage.put(email, otp);


        // Send email

        emailService.sendOtp(email, otp);


        response.put(
                "message",
                "OTP sent successfully"
        );

        return response;
    }


    // =========================
    // VERIFY OTP
    // =========================

    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(
            @RequestBody Map<String, String> request) {

        String email =
                request.get("email");

        String otp =
                request.get("otp");


        Map<String, Object> response =
                new HashMap<>();


        String storedOtp =
                otpStorage.get(email);


        if (storedOtp == null) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    "OTP expired or not requested"
            );

            return response;
        }


        if (!storedOtp.equals(otp)) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    "Invalid OTP"
            );

            return response;
        }


       
     // OTP correct

        otpStorage.remove(email);

        User user =
                userService.getUserByEmail(email);

        // Generate JWT
        String token =
                jwtService.generateToken(user.getEmail(),"User");

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Login successful"
        );

        response.put(
                "token",
                token
        );

        response.put(
                "user",
                user
        );

        return response;

    }
}