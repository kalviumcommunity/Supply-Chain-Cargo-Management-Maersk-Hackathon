package com.cargomanagement.controller;

import com.cargomanagement.models.User;
import com.cargomanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {
    "http://localhost:5173", 
    "http://localhost:5174",
    "http://cargo-flow.s3-website.ap-south-1.amazonaws.com"
}, allowCredentials = "true")
public class UserManagementController {

    private final AuthService authService;

    public UserManagementController(AuthService authService) {
        this.authService = authService;
    }

    // Check if current user is admin
    private boolean isAdmin(Authentication authentication, HttpServletRequest request) {
        System.out.println("=== Admin Check Debug ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));
        
        // Check OAuth2 authentication
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            System.out.println("Principal type: " + (principal != null ? principal.getClass().getName() : "null"));
            
            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                String email = oAuth2User.getAttribute("email");
                System.out.println("OAuth2 Email: " + email);
                Optional<User> userOpt = authService.findByEmail(email);
                if (userOpt.isPresent()) {
                    System.out.println("User Role: " + userOpt.get().getRole());
                    return "ADMIN".equals(userOpt.get().getRole()) && userOpt.get().getIsActive();
                }
            }
        }
        
        // Check session-based authentication (local login)
        HttpSession session = request.getSession(false);
        System.out.println("Session: " + session);
        if (session != null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("User ID in session: " + session.getAttribute("userId"));
            System.out.println("User Email in session: " + session.getAttribute("userEmail"));
            
            if (session.getAttribute("userEmail") != null) {
                String email = (String) session.getAttribute("userEmail");
                Optional<User> userOpt = authService.findByEmail(email);
                if (userOpt.isPresent()) {
                    System.out.println("Found user: " + userOpt.get().getEmail() + ", Role: " + userOpt.get().getRole());
                    return "ADMIN".equals(userOpt.get().getRole()) && userOpt.get().getIsActive();
                } else {
                    System.out.println("User not found for email: " + email);
                }
            }
        }
        
        System.out.println("Admin check failed - no valid authentication found");
        return false;
    }

    // Get all pending users (admin only)
    @GetMapping("/pending-users")
    public ResponseEntity<?> getPendingUsers(
            Authentication authentication,
            HttpServletRequest request) {
        
        try {
            System.out.println("=== GET /api/admin/pending-users called ===");
            
            if (!isAdmin(authentication, request)) {
                System.out.println("Admin check failed - returning 403");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Admin access required"));
            }

            System.out.println("Admin check passed - fetching pending users");
            List<User> pendingUsers = authService.getPendingUsers();
            System.out.println("Found " + pendingUsers.size() + " pending users");
            
            // Remove password from response
            pendingUsers.forEach(user -> user.setPassword(null));
            
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            System.err.println("Error in getPendingUsers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to fetch pending users: " + e.getMessage(),
                    "error", e.getClass().getName()
                ));
        }
    }

    // Get all users (admin only)
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(
            Authentication authentication,
            HttpServletRequest request) {
        
        if (!isAdmin(authentication, request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        List<User> allUsers = authService.getAllUsers();
        
        // Remove passwords from response
        allUsers.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(allUsers);
    }

    // Approve user (admin only)
    @PostMapping("/approve-user/{userId}")
    public ResponseEntity<?> approveUser(
            @PathVariable Long userId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        if (!isAdmin(authentication, httpRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        try {
            User user = authService.approveUser(userId);
            user.setPassword(null); // Remove password from response
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User approved successfully",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Reject user (admin only)
    @DeleteMapping("/reject-user/{userId}")
    public ResponseEntity<?> rejectUser(
            @PathVariable Long userId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        if (!isAdmin(authentication, httpRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        try {
            authService.rejectUser(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User rejected and deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
