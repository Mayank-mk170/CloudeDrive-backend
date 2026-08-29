package com.cloudstorage.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;


//@Service
//public class JWTService {
//
//    @Value("${jwt.algorithm.key}")
//    private String algorithmKey;
//
//    @Value("${jwt.issuer}")
//    private String issuer;
//
//    @Value("${jwt.expiry.duration}")
//    private int expiryTime;
//
//    private Algorithm algorithm;
//
//
//    @PostConstruct
//    public void postConstruct() throws UnsupportedEncodingException {
//        algorithm = Algorithm.HMAC256(algorithmKey);
//    }
//
//    public String generateToken(String email){
//        return JWT.create()
//                .withClaim("name",email)
//                .withExpiresAt(new Date(System.currentTimeMillis()+expiryTime))
//                .withIssuer(issuer)
//                .sign(algorithm);
//    }
//    public String getEmail(String token) {
//        DecodedJWT decodedJWT = JWT.
//                require(algorithm)
//                .withIssuer(issuer)
//                .build()
//                .verify(token);
//        return decodedJWT.getClaim("email").asString();
//    }
//
//
//}

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry.duration}")
    private long expiryTime;

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() throws UnsupportedEncodingException {

        if (algorithmKey == null || algorithmKey.isBlank()) {
            throw new IllegalStateException(
                    "JWT_ALGORITHM_KEY is missing"
            );
        }

        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException(
                    "JWT_ISSUER is missing"
            );
        }

        algorithm = Algorithm.HMAC256(algorithmKey);

        System.out.println("=================================");
        System.out.println("JWT SERVICE INITIALIZED");
        System.out.println("JWT ISSUER = " + issuer);
        System.out.println("JWT KEY PRESENT = true");
        System.out.println("JWT EXPIRY = " + expiryTime);
        System.out.println("=================================");
    }

    // ==========================================
    // GENERATE TOKEN
    // ==========================================

    public String generateToken(String email) {

        return JWT.create()
                .withClaim("email", email)
                .withExpiresAt(
                        new Date(
                                System.currentTimeMillis()
                                        + expiryTime
                        )
                )
                .withIssuer(issuer)
                .sign(algorithm);
    }

    // ==========================================
    // GET EMAIL FROM TOKEN
    // ==========================================

    public String getEmail(String token) {

        DecodedJWT decodedJWT =
                JWT.require(algorithm)
                        .withIssuer(issuer)
                        .build()
                        .verify(token);

        return decodedJWT
                .getClaim("email")
                .asString();
    }
}