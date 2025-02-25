package org.example.cooking_app.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.Random;

@Service
public class EmailService {
    public String sendVerificationCode(String toEmail) throws MessagingException {
        String code = generateCode();
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        String username = "sabinakazakova783@gmail.com";
        String appPassword = "lmkxedgzvibomeii";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject("Your Verification Code");

        String content = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        .code {
                            font-size: 1.5rem;
                            font-weight: bold;
                            color: #6a11cb;
                            background: #f4f4f9;
                            padding: 10px;
                            border: 1px dashed #6a11cb;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <h3>Your Verification Code:</h3>
                    <p>Please use the code below to complete your process:</p>
                    <div class="code">%s</div>
                    <p>If you did not request this, please ignore this email.</p>
                </body>
                </html>
                """.formatted(code);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("Email sent successfully!");
        return code;
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
