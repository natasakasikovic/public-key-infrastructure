package com.security.pki.shared.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.security.pki.shared.exceptions.EmailSendingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${app.base.url}")
    private String baseUrl;

    private static final String SUBJECT = "Account Activation";

    public void sendVerificationEmail(String to, String token) {
        Mail mail = buildVerificationMail(to, token);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() != 202) {
                throw new EmailSendingException("Failed to send email. Response: " + response.getBody());
            }

        } catch (Exception ex) {
            throw new EmailSendingException("Error while sending verification email to " + to, ex);
        }
    }

    private Mail buildVerificationMail(String to, String token) {
        Email from = new Email(fromEmail);
        Email recipient = new Email(to);

        String activationLink = baseUrl + "/api/v1/users/activation?token=" + token;

        Content content = new Content(
                "text/html",
                "<p>Welcome!</p><p>Please click <a href='" + activationLink + "'>here</a> to activate your account.</p>"
        );

        return new Mail(from, SUBJECT, recipient, content);
    }
}
