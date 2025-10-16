package com.company.payments.controller;

import com.company.payments.model.User;
import com.company.payments.model.response.ApiResponse;
import com.company.payments.model.response.AuthResponse;
import com.company.payments.security.JwtUtil;
import com.company.payments.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;


    @Autowired
    private CustomUserDetailsService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@RequestBody User user) {

        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            String msj = "Credenciales invalidas";
            throw new BadCredentialsException (msj) {};
        }

        String time = userService.updateLastLogin(userDetails.getUsername());

        AuthResponse authResponse = new AuthResponse(
                jwtUtil.generateToken(userDetails.getUsername()),
                time);
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                "200",
                "Success",
                authResponse);
        return ResponseEntity.ok(response);  // 200 OK


    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user)) {
            throw new IllegalArgumentException("User already exists") {};
        }
        final User newUser = new User(
                null,
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userService.save(newUser);
        return "User registered successfully";

    }
}
