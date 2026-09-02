package com.cloudstorage.controller;


import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.dto.RegisterRequest;
import com.cloudstorage.dto.TokenDto;
import com.cloudstorage.model.User;
import com.cloudstorage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {

        return userService.createUser(registerRequest);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {

        String token =
                userService.verifyLogin(loginRequest);


        if (token != null) {

            TokenDto tokenDto =
                    new TokenDto();

            tokenDto.setToken(token);

            tokenDto.setType("JWT");


            return new ResponseEntity<>(
                    tokenDto,
                    HttpStatus.OK
            );

        } else {

            return new ResponseEntity<>(
                    "Invalid email/password",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getCurrentUser(
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        return new ResponseEntity<>(
                user,
                HttpStatus.OK
        );
    }

    // Add this to UserController.java

    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSuccess(
            @RequestParam String token
    ) {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);
        tokenDto.setType("JWT");
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }
}
