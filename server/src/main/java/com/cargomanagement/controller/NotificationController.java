package com.cargomanagement.controller;

import com.cargomanagement.dto.NotificationRequest;
import com.cargomanagement.service.NotificationService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmailNotification(@Valid @RequestBody NotificationRequest request) {
        boolean sent = notificationService.sendCustomEmail(request.getRecipients(), request.getSubject(), request.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", sent);
        response.put("subject", request.getSubject());
        response.put("message", sent ? "Notification email dispatched." : "Notification skipped - no recipients configured.");
        HttpStatus status = sent ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
