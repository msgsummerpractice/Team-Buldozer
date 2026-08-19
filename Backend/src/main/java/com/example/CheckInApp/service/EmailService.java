package com.example.CheckInApp.service;

import com.example.CheckInApp.exception.EmailDeliveryException;
import com.example.CheckInApp.model.Event;
import com.example.CheckInApp.model.EventLocation;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Async
    public void notifyEventPublished(Event event) {
        List<User> failed = sendToAll(event, resolveRecipients(event.getLocation()));

        if (!failed.isEmpty()) {
            failed = retryFailed(event, failed, 2);
        }

        if (!failed.isEmpty()) {
            throw new EmailDeliveryException(failed.stream().map(User::getEmail).toList());
        }
    }

    private List<User> sendToAll(Event event, List<User> recipients) {
        return recipients.stream()
                .filter(recipient -> !trySendEmail(event, recipient))
                .toList();
    }

    private List<User> retryFailed(Event event, List<User> failed, int maxRetries) {
        for (int i = 0; i < maxRetries && !failed.isEmpty(); i++) {
            failed = sendToAll(event, failed);
        }
        return failed;
    }

    private List<User> resolveRecipients(EventLocation eventLocation) {
        if (eventLocation == EventLocation.ALL) {
            return userRepository.findAll();
        }
        UserLocation targetLocation = UserLocation.valueOf(eventLocation.name());
        return userRepository.findByLocation(targetLocation);
    }

    private boolean trySendEmail(Event event, User recipient) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient.getEmail());
            helper.setSubject("New event: " + event.getName());
            helper.setText(buildHtmlBody(event), true);

            if (event.getPoster() != null) {
                helper.addInline("poster", new org.springframework.core.io.ByteArrayResource(event.getPoster()), "image/jpeg");
            }

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    private String buildHtmlBody(Event event) {
        return """
                <html>
                <body>
                  <h2>%s</h2>
                  <p><strong>Date:</strong> %s</p>
                  <p><strong>Location:</strong> %s</p>
                  %s
                </body>
                </html>
                """.formatted(
                event.getName(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getLocation(),
                event.getPoster() != null ? "<img src=\"cid:poster\" alt=\"Event poster\" style=\"max-width:600px;\"/>" : ""
        );
    }
}
