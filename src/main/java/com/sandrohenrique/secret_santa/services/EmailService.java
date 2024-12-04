package com.sandrohenrique.secret_santa.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendEmail(String groupName, String eventLocation, String eventDate, Float spendingCap, String recipientEmail, String recipientName, String drawnFriendName, List<String> drawnFriendWishlist) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(recipientEmail);
            simpleMailMessage.setSubject(groupName);
            String message = """
Olá, %s!
\s
Você foi convidado para participar de um amigo oculto no grupo %s.
\s
Detalhes do evento:
- Local: %s
- Data: %s
- Limite de gastos: R$ %.2f
\s
Seu amigo oculto é: %s
\s
Segue a lista de desejos do seu amigo:
%s
\s
Divirta-se e boas festas!
       \s""".formatted(
                    recipientName,
                    groupName,
                    eventLocation,
                    eventDate,
                    spendingCap,
                    drawnFriendName,
                    String.join("\n", drawnFriendWishlist.stream().map(item -> "- " + item).toList()));

            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception exception) {
            exception.getMessage();
        }
    }
}
