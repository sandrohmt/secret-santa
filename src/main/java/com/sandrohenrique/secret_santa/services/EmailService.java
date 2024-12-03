package com.sandrohenrique.secret_santa.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public String sendTextEmail(String groupName, String eventLocation, LocalDate eventDate, Float spendingCap, String recipientEmail, String recipientName, String drawnFriendName, List<String> drawnFriendWishlist) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(recipientEmail);
            simpleMailMessage.setSubject(groupName);
            String message = """
                    Olá, %s!
                    
                    Você foi convidado para participar de um amigo oculto no grupo "s.
                    
                    Detalhes do evento:
                    - Local: %s
                    - Data: %s
                    - Limite de gastos: R$ %.2f
                    
                    Seu amigo oculto é: %s
                    
                    Segue a lista de desejos do seu amigo:
                    - %s
                    
                    Divirta-se e boas festas!
        """.formatted(recipientName, groupName, eventLocation, eventDate, spendingCap, drawnFriendName, String.join("\n- ", drawnFriendWishlist));

            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
            return "Email enviado";
        } catch (Exception exception) {
            return "Erro ao tentar enviar email!" + exception.getMessage();
        }
    }
}
