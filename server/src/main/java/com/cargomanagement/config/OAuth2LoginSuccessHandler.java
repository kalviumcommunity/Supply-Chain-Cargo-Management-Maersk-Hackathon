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
        
        // Redirect to frontend OAuth callback
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/oauth-callback");
    }
}
