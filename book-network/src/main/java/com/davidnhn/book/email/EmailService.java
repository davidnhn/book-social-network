package com.davidnhn.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplate emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        // Détermination du nom du template à utiliser
        if(emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName  = emailTemplate.getName();
        }

        // Création d'un nouveau message email conforme au standard MIME (Multipurpose Internet Mail Extensions)
        // MimeMessage permet de créer des emails complexes pouvant contenir du texte, du HTML, et des pièces jointes
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        // Création d'un assistant (helper) pour faciliter la configuration du message MIME
        // Le MimeMessageHelper simplifie la création et la manipulation des messages email complexes
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,  // Le message MIME à configurer
                MimeMessageHelper.MULTIPART_MODE_MIXED,  // Mode multipart permettant différents types de contenu dans l'email
                // MULTIPART_MODE_MIXED permet d'inclure du texte, du HTML et des pièces jointes dans le même email
                StandardCharsets.UTF_8.name()  // Utilisation de l'encodage UTF-8 pour le support de tous les caractères
        );

        // L'utilisation de MimeMessageHelper avec ces paramètres permet de :
        // 1. Créer des emails avec plusieurs parties (texte, HTML, pièces jointes)
        // 2. Assurer une compatibilité maximale avec différents clients email
        // 3. Supporter correctement les caractères internationaux grâce à l'encodage UTF-8

        // Création d'une map pour stocker les propriétés du template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        // Création d'un contexte Thymeleaf et ajout des propriétés
        Context context = new Context();
        context.setVariables(properties);

        // Configuration de l'expéditeur, du destinataire et du sujet de l'e-mail
        helper.setFrom("contact@booksocialnetwork.com");
        helper.setTo(to);
        helper.setSubject(subject);

        // Traitement du template avec Thymeleaf
        String template = templateEngine.process(templateName, context);
        // Configuration du contenu de l'e-mail
        helper.setText(template, true);
        mailSender.send(mimeMessage);
    }

}
