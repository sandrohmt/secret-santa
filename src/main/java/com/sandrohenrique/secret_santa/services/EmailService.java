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

    public String sendTextEmail(String recipientEmail, String recipientName, String subject, String drawnFriendName, List<String> drawnFriendWishlist) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(recipientEmail);
            simpleMailMessage.setSubject(subject);
            String message = """
                    Olá, %s!
                    
                    Você foi convidado para participar de um amigo oculto!
                    Seu amigo oculto é: %s
                    
                    Segue a lista de desejos dele:
                    - %s
                    
                    Divirta-se e boas festas!
                    """.formatted(recipientName, drawnFriendName, drawnFriendWishlist);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
            return "Email enviado";
        } catch (Exception exception) {
            return "Erro ao tentar enviar email!" + exception.getMessage();
        }
    }
}
