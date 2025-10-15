package com.cargomanagement.config;

import com.cargomanagement.models.User;
import com.cargomanagement.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    public OAuth2LoginSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // Extract user information from OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        if (email != null) {
            // Check if user exists, if not create new user
            Optional<User> existingUser = authService.findByEmail(email);
            User user;
            
            if (existingUser.isEmpty()) {
                // Create new user for OAuth login
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setPicture(picture);
                user.setProvider("GOOGLE");
                user.setRole("OPERATOR");
                user.setIsActive(true);
                user = authService.saveUser(user);
            } else {
                user = existingUser.get();
                // Update picture if it changed
                if (picture != null && !picture.equals(user.getPicture())) {
                    user.setPicture(picture);
                    user = authService.saveUser(user);
                }
            }
            
            // Store user info in session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
        }
        
        // Determine the frontend URL to redirect to based on the request origin
        String redirectUrl = determineRedirectUrl(request);
        
        // Redirect to frontend OAuth callback
        getRedirectStrategy().sendRedirect(request, response, redirectUrl + "/oauth-callback");
    }
    
    private String determineRedirectUrl(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        
        // Check if request came from AWS deployment
        if (origin != null && origin.contains("cargo-flow.s3-website.ap-south-1.amazonaws.com")) {
            return "http://cargo-flow.s3-website.ap-south-1.amazonaws.com";
        }
        if (referer != null && referer.contains("cargo-flow.s3-website.ap-south-1.amazonaws.com")) {
            return "http://cargo-flow.s3-website.ap-south-1.amazonaws.com";
        }
        
        // Check for localhost:5174
        if (origin != null && origin.contains("localhost:5174")) {
            return "http://localhost:5174";
        }
        if (referer != null && referer.contains("localhost:5174")) {
            return "http://localhost:5174";
        }
        
        // Default to localhost:5173
        return "http://localhost:5173";
    }
}
