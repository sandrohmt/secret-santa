package com.sandrohenrique.secret_santa.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("sendEmail send emails to each Friend when successful")
    void sendEmail_SendEmailsToEachFriend_WhenSuccessful() {
        String groupName = "Amigo Secreto";
        String eventLocation = "Rua das Flores, 123";
        String eventDate = "20/12/2024";
        Float spendingCap = 100.0f;
        String recipientEmail = "invalid-email"; // Email inválido
        String recipientName = "Maria";
        String drawnFriendName = "José";
        List<String> drawnFriendWishlist = List.of("PlayStation 5", "Câmera");

        emailService.sendEmail(
                groupName, eventLocation, eventDate, spendingCap, recipientEmail, recipientName, drawnFriendName, drawnFriendWishlist);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}