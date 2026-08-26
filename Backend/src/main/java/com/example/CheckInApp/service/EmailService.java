package com.example.CheckInApp.service;

import com.example.CheckInApp.exception.EmailDeliveryException;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.util.HtmlUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {

    private static final int MAX_EMAIL_RETRIES = 2;
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final ZoneId EMAIL_ZONE = ZoneId.of("Europe/Bucharest");
    private static final DateTimeFormatter EMAIL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_ONLY_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final String frontendUrl;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public EmailService(@Value("${app.frontend-url}") String frontendUrl,
                        JavaMailSender mailSender,
                        UserRepository userRepository) {
        this.frontendUrl = frontendUrl;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Async
    public void notifyEventPublished(Event event) {
        List<String> failed = sendToAll(event, resolveRecipients(event.getLocation()));

        if (!failed.isEmpty()) {
            failed = retryFailed(event, failed, MAX_EMAIL_RETRIES);
        }

        if (!failed.isEmpty()) {
            throw new EmailDeliveryException(failed);
        }
    }

    private List<String> sendToAll(Event event, List<String> recipients) {
        return recipients.stream()
                .filter(email -> !trySendEmail(event, email))
                .toList();
    }

    private List<String> retryFailed(Event event, List<String> failed, int maxRetries) {
        for (int i = 0; i < maxRetries && !failed.isEmpty(); i++) {
            failed = sendToAll(event, failed);
        }
        return failed;
    }

    private List<String> resolveRecipients(EventLocation eventLocation) {
        if (eventLocation == EventLocation.ALL) {
            return userRepository.findActiveEmails();
        }
        UserLocation targetLocation = UserLocation.valueOf(eventLocation.name());
        return userRepository.findActiveEmailsByLocation(targetLocation);
    }

    private boolean trySendEmail(Event event, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("New event: " + event.getName());
            helper.setText(buildHtmlBody(event), true);

            if (event.getPoster() != null) {
                byte[] poster = event.getPoster();
                helper.addInline("poster", new ByteArrayResource(poster), detectMimeType(poster));
            }

            mailSender.send(message);
            return true;
        } catch (MessagingException | MailException e) {
            return false;
        }
    }

    private String formatDateRange(Event event) {
        Instant startInstant = event.getStartDateTime();
        Instant endInstant = event.getEndDateTime();
        ZonedDateTime bucharestStart = startInstant.atZone(EMAIL_ZONE);
        if (endInstant == null) {
            return bucharestStart.format(EMAIL_DATE_FORMAT);
        }
        ZonedDateTime bucharestEnd = endInstant.atZone(EMAIL_ZONE);
        if (bucharestStart.toLocalDate().equals(bucharestEnd.toLocalDate())) {
            return bucharestStart.format(EMAIL_DATE_FORMAT) + " – " + bucharestEnd.format(TIME_ONLY_FORMAT);
        }
        return bucharestStart.format(EMAIL_DATE_FORMAT) + " – " + bucharestEnd.format(EMAIL_DATE_FORMAT);
    }

    private static String detectMimeType(byte[] data) {
        return data.length >= 4 && Arrays.equals(data, 0, 4, PNG_MAGIC, 0, 4) ? "image/png" : "image/jpeg";
    }

    @Async
    public void sendPasswordResetEmail(String email, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Reset your password");
            helper.setText(buildPasswordResetHtml(resetUrl), true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException(List.of(email));
        }
    }

    private String buildPasswordResetHtml(String resetUrl) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                  <h2>Password Reset Request</h2>
                  <p>We received a request to reset your password.</p>
                  <p>Click the button below to set a new password. This link will expire in 15 minutes.</p>
                  <a href="%s" target="_blank" style="text-decoration: none; display: inline-block; margin-top: 15px;">
                    <button type="button" style="
                      background-color: #8e1239;
                      color: #ffffff;
                      font-family: Arial, sans-serif;
                      font-size: 16px;
                      font-weight: bold;
                      padding: 12px 24px;
                      border: none;
                      border-radius: 6px;
                      cursor: pointer;">
                      Reset Password
                    </button>
                  </a>
                  <p style="margin-top: 24px;">If you didn't request a password reset, you can safely ignore this email.</p>
                </body>
                </html>
                """.formatted(resetUrl);
    }

    private String buildHtmlBody(Event event) {
        String detailsUrl = frontendUrl + "/events/" + event.getId() + "/details";
        String posterHtml = event.getPoster() != null
                ? "<img src=\"cid:poster\" alt=\"Event poster\" style=\"max-width:600px;\"/>"
                : "";
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                  %s
                  <h2>%s</h2>
                  <p>%s</p>
                  <p>%s</p>
                  <a href="%s" target="_blank" style="text-decoration: none; display: inline-block; margin-top: 15px;">
                    <button type="button" style="
                      background-color: #8e1239;
                      color: #ffffff;
                      font-family: Arial, sans-serif;
                      font-size: 16px;
                      font-weight: bold;
                      padding: 12px 24px;
                      border: none;
                      border-radius: 6px;
                      cursor: pointer;">
                      View Event
                    </button>
                  </a>
                </body>
                </html>
                """.formatted(
                posterHtml,
                HtmlUtils.htmlEscape(event.getName()),
                formatDateRange(event),
                HtmlUtils.htmlEscape(event.getLocation().toString()),
                detailsUrl
        );
    }
}
