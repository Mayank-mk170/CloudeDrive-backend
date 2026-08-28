package com.cloudstorage.service;


import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.dto.RegisterRequest;
import com.cloudstorage.model.Role;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private JWTService jwtService;

    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> createUser(
            RegisterRequest registerRequest
    ){
        Optional<User> opEmail =
                userRepository.findByEmail(
                        registerRequest.email()
                );
        if(opEmail.isPresent()){
            return new ResponseEntity<>(
                    "Email already register",
                    HttpStatus.CONFLICT
            );
        }

        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setRole(Role.USER);

        // Encrypt password
        String encryptedPassword = BCrypt.hashpw(
                registerRequest.password(),
                BCrypt.gensalt(10)
        );
        user.setPassword(encryptedPassword);

        // save user

        User saveUser = userRepository.save(user);

        return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
    }

    // Login

    public String verifyLogin(LoginRequest loginRequest){

        Optional<User> opUser = userRepository.findByEmail(
                loginRequest.email()
        );
        if (opUser.isPresent()) {

            User user = opUser.get();

            if(user.getPassword()== null){
                return null;
            }


            boolean passwordMatches =
                    BCrypt.checkpw(
                            loginRequest.password(),
                            user.getPassword()
                    );


            if (passwordMatches) {

                // Generate JWT

                return jwtService.generateToken(user.getEmail());

            }
        }


        return null;


    }

    // Get User
    public User getUserByEmail(String email){
        Optional<User> user =
                userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public ResponseEntity<?> getCurrentUser(String email) {
        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            return new ResponseEntity<>(
                    user,
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                "User not found",
                HttpStatus.NOT_FOUND
        );
    }

    // ==========================================
// GOOGLE OAUTH2 USER
// ==========================================

    public User findOrCreateGoogleUser(
            String name,
            String email,
            String providerId
    ) {

        // Check whether user already exists
        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            User existingUser =
                    optionalUser.get();

            // Update Google information if necessary
            existingUser.setProvider("GOOGLE");
            existingUser.setProviderId(providerId);

            // If existing user doesn't have a role,
            // give them the default USER role.
            if (existingUser.getRole() == null) {

                existingUser.setRole(
                        Role.USER
                );
            }

            return userRepository.save(existingUser);
        }

        // Create new Google user
        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Google users don't have an application password
        user.setPassword(null);

        user.setProvider("GOOGLE");
        user.setProviderId(providerId);

        user.setRole(Role.USER);

        return userRepository.save(user);
    }
}
