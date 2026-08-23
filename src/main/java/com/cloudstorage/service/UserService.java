package com.cloudstorage.service;


import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.dto.RegisterRequest;
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

    public String verifyLogin(
            LoginRequest loginRequest){

        Optional<User> opUser = userRepository.findByEmail(
                loginRequest.email()
        );
        if (opUser.isPresent()) {

            User user = opUser.get();


            boolean passwordMatches =
                    BCrypt.checkpw(
                            loginRequest.password(),
                            user.getPassword()
                    );


            if (passwordMatches) {

                // Generate JWT

                String token =
                        jwtService.generateToken(
                                user.getEmail()
                        );

                return token;
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
}
