package com.cloudstorage.config;

import com.cloudstorage.model.User;
import com.cloudstorage.service.JWTService;
import com.cloudstorage.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
@Configuration
public class GoogleOAuth2 extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTService jwtService;



    public GoogleOAuth2(
            UserService userService,
            JWTService jwtService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        // Get Google account information
        String email =
                oauth2User.getAttribute("email");

        String name =
                oauth2User.getAttribute("name");

        String providerId =
                oauth2User.getAttribute("sub");

        // Google email is required
        if (email == null || email.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Google email not found"
            );

            return;
        }

        // Google name fallback
        if (name == null || name.isBlank()) {
            name = "Google User";
        }

        // Find existing user or create new user
        User user =
                userService.findOrCreateGoogleUser(
                        name,
                        email,
                        providerId
                );

        // Generate your application's JWT
        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        /*
         * Frontend URL
         *
         * For local development:
         * http://localhost:5173
         *
         * Change the port through FRONTEND_URL
         * instead of changing Java code.
         */
        String frontendUrl =
                System.getenv("FRONTEND_URL");

        if (frontendUrl == null ||
                frontendUrl.isBlank()) {

            frontendUrl =
                    "http://localhost:5173";
        }

        // Remove trailing slash
        frontendUrl =
                frontendUrl.replaceAll("/+$", "");

        // Security check:
        // Only allow localhost HTTP frontend
        if (!frontendUrl.matches(
                "^http://localhost:\\d+$"
        )) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid frontend URL"
            );

            return;
        }

        // Redirect to React frontend
        String redirectUrl =
                frontendUrl
                        + "/oauth-success?token="
                        + token;

        getRedirectStrategy().sendRedirect(
                request,
                response,
                redirectUrl
        );
    }
}