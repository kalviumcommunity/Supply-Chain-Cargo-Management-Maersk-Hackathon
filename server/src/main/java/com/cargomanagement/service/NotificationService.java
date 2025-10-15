package com.cargomanagement.service;

import com.cargomanagement.models.Cargo;
import com.cargomanagement.models.Delivery;
import com.cargomanagement.models.Shipment;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;
    private final List<String> defaultRecipients;
    private final String fromAddress;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${notification.email.recipients:}") String recipients,
                               @Value("${notification.email.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.defaultRecipients = parseRecipients(recipients);
        this.fromAddress = fromAddress;
    }

    @PostConstruct
    void logConfiguration() {
        if (defaultRecipients.isEmpty()) {
            log.warn("Email notifications are configured but no recipients were provided. Set 'notification.email.recipients' to enable outgoing emails.");
        } else {
            log.info("Email notifications enabled for recipients: {}", defaultRecipients);
        }
    }

    public void notifyCargoCreated(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Created - #" + cargo.getCargoId();
        String body = buildCargoMessage("A new cargo record was created.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyCargoUpdated(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Updated - #" + cargo.getCargoId();
        String body = buildCargoMessage("A cargo record was updated.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyCargoDeleted(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Deleted - #" + cargo.getCargoId();
        String body = buildCargoMessage("A cargo record was deleted.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyShipmentCreated(Shipment shipment) {
        if (shipment == null) {
            return;
        }
        String subject = "Shipment Created - #" + shipment.getShipmentId();
        String body = buildShipmentMessage("A new shipment was created.", shipment, null);
        sendNotification(null, subject, body);
    }

    public void notifyShipmentUpdated(Shipment shipment, String previousStatus) {
        if (shipment == null) {
            return;
        }
        String subject = "Shipment Updated - #" + shipment.getShipmentId();
        String body = buildShipmentMessage("A shipment was updated.", shipment, previousStatus);
        sendNotification(null, subject, body);
    }

    public void notifyShipmentDeleted(Shipment shipment) {
        if (shipment == null) {
            return;
        }
        String subject = "Shipment Deleted - #" + shipment.getShipmentId();
        String body = buildShipmentMessage("A shipment was deleted.", shipment, null);
        sendNotification(null, subject, body);
    }

    public void notifyDeliveryEvent(Delivery delivery, String action) {
        if (delivery == null) {
            return;
        }
        String subject = "Delivery " + action + " - #" + delivery.getDeliveryId();
        StringBuilder body = new StringBuilder()
            .append("Delivery was ").append(action.toLowerCase()).append(".\n\n")
            .append("Recipient: ").append(delivery.getRecipient()).append('\n');

        if (StringUtils.hasText(delivery.getStatus())) {
            body.append("Status: ").append(delivery.getStatus()).append('\n');
        }

        if (delivery.getActualDeliveryDate() != null) {
            body.append("Completed At: ")
                .append(delivery.getActualDeliveryDate().format(DATE_TIME_FORMATTER))
                .append('\n');
        }

        if (delivery.getShipment() != null) {
            body.append("Shipment Destination: ")
                .append(delivery.getShipment().getDestination())
                .append('\n');

            if (StringUtils.hasText(delivery.getShipment().getStatus())) {
                body.append("Shipment Status: ")
                    .append(delivery.getShipment().getStatus())
                    .append('\n');
            }

            if (StringUtils.hasText(delivery.getShipment().getShipmentCode())) {
                body.append("Shipment Code: ")
                    .append(delivery.getShipment().getShipmentCode())
                    .append('\n');
            }
        }

        sendNotification(null, subject, body.toString());
    }

    public boolean sendCustomEmail(List<String> recipients, String subject, String message) {
        return sendNotification(recipients, subject, message);
    }

    private String buildCargoMessage(String header, Cargo cargo) {
        StringBuilder body = new StringBuilder(header)
            .append('\n').append('\n')
            .append("Type: ").append(cargo.getType()).append('\n')
            .append("Value: $").append(cargo.getValue()).append('\n');

        if (cargo.getWeight() != null) {
            body.append("Weight: ").append(cargo.getWeight());
            if (StringUtils.hasText(cargo.getWeightUnit())) {
                body.append(' ').append(cargo.getWeightUnit());
            }
            body.append('\n');
        }

        if (cargo.getVolume() != null) {
            body.append("Volume: ").append(cargo.getVolume()).append('\n');
        }

        if (StringUtils.hasText(cargo.getDescription())) {
            body.append("Description: ").append(cargo.getDescription()).append('\n');
        }

        if (cargo.getShipment() != null) {
            body.append("Linked Shipment ID: ").append(cargo.getShipment().getShipmentId()).append('\n');
            if (StringUtils.hasText(cargo.getShipment().getShipmentCode())) {
                body.append("Shipment Code: ").append(cargo.getShipment().getShipmentCode()).append('\n');
            }
        }

        return body.toString();
    }

    private String buildShipmentMessage(String header, Shipment shipment, String previousStatus) {
        StringBuilder body = new StringBuilder(header)
            .append('\n').append('\n')
            .append("Origin: ").append(shipment.getOrigin()).append('\n')
            .append("Destination: ").append(shipment.getDestination()).append('\n')
            .append("Status: ").append(shipment.getStatus()).append('\n');

        if (StringUtils.hasText(previousStatus) && !Objects.equals(previousStatus, shipment.getStatus())) {
            body.append("Previous Status: ").append(previousStatus).append('\n');
        }

        if (shipment.getEstimatedDelivery() != null) {
            body.append("Estimated Delivery: ")
                .append(shipment.getEstimatedDelivery().format(DATE_FORMATTER))
                .append('\n');
        }

        if (shipment.getAssignedRoute() != null) {
            body.append("Route ID: ").append(shipment.getAssignedRoute().getRouteId()).append('\n');
        }

        if (shipment.getAssignedVendor() != null) {
            body.append("Vendor: ").append(shipment.getAssignedVendor().getName()).append('\n');
        }

        if (StringUtils.hasText(shipment.getShipmentCode())) {
            body.append("Shipment Code: ").append(shipment.getShipmentCode()).append('\n');
        }

        return body.toString();
    }

    private boolean sendNotification(List<String> overrideRecipients, String subject, String body) {
        List<String> recipients = resolveRecipients(overrideRecipients);
        if (recipients.isEmpty()) {
            log.debug("Skipping email notification '{}' because no recipients are configured.", subject);
            return false;
        }

        if (!StringUtils.hasText(subject) || !StringUtils.hasText(body)) {
            log.warn("Attempted to send email notification with missing subject or body. Skipping.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(recipients.toArray(String[]::new));
            if (StringUtils.hasText(fromAddress)) {
                helper.setFrom(fromAddress);
            }
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            log.info("Sent email notification '{}' to {}", subject, recipients);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email notification '{}': {}", subject, ex.getMessage(), ex);
            return false;
        }
    }

    private List<String> resolveRecipients(List<String> overrideRecipients) {
        List<String> parsedOverride = parseRecipients(overrideRecipients);
        if (!parsedOverride.isEmpty()) {
            return parsedOverride;
        }
        return defaultRecipients;
    }

    private List<String> parseRecipients(List<String> recipients) {
        if (recipients == null) {
            return Collections.emptyList();
        }
        List<String> parsed = new ArrayList<>();
        recipients.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .map(String::toLowerCase)
            .forEach(address -> {
                if (!parsed.contains(address)) {
                    parsed.add(address);
                }
            });
        return parsed;
    }

    private List<String> parseRecipients(String csvRecipients) {
        if (!StringUtils.hasText(csvRecipients)) {
            return Collections.emptyList();
        }
        return Stream.of(csvRecipients.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(String::toLowerCase)
            .distinct()
            .toList();
    }
}
