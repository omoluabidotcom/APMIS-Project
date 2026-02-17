package com.cinoteck.application.views.utils.email;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class SendGridEmailService {

    private final String apiKey;
    private final String fromEmail;
    private final String fromName;
    private final String appBaseUrl;
    
    
    public SendGridEmailService(
           String apiKey,
            @Value("${mail.from.email}") String fromEmail,
            @Value("${mail.from.name}") String fromName,
            @Value("${app.base.url}") String appBaseUrl) {

        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.appBaseUrl = appBaseUrl;

        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("SendGrid API key not configured. Check application.properties or environment variables.");
        }
    }



    
    public void sendPasswordResetToken(String toEmail, String token) throws IOException {
    	
    	System.out.println("XXXXXXXXXXXXXXXX_________________XXXXXXXXXXXXX");
    	
    	
        Objects.requireNonNull(toEmail, "toEmail must not be null");
        Objects.requireNonNull(token, "token must not be null");

        String resetLink = appBaseUrl + "/resetuserpassword?token=" + urlEncode(token);
        String subject = "Reset your APMIS password";
        String html = buildHtml(resetLink, token);
        String text = buildText(resetLink, token);

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);

        // ✅ text/plain must come first
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        Personalization personalization = new Personalization();
        personalization.addTo(to);
        mail.addPersonalization(personalization);

        // Add plain text first
        mail.addContent(new Content("text/plain", text));
        // Add HTML second
        mail.addContent(new Content("text/html", html));

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        System.out.println("SendGrid Endpoint---: " + request.getEndpoint());

        Response response = sg.api(request);

        int status = response.getStatusCode();
        System.out.println("SendGrid Status: " + status);
        System.out.println("SendGrid Response Body: " + response.getBody());
        System.out.println("SendGrid Headers: " + response.getHeaders());

        if (status < 200 || status >= 300) {
            throw new IOException("SendGrid send failed: HTTP " + status + " - " + response.getBody());
        }
    }


    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private static String buildHtml(String resetLink, String token) {
        return "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#111\">" +
                "<p>Hello,</p>" +
                "<p>We received a request to reset your password. Click the button below to proceed:</p>" +
                "<p style=\"margin:22px 0\">" +
                "<a href=\"" + resetLink + "\" style=\"background:#2563eb;color:#fff;text-decoration:none;padding:10px 16px;border-radius:6px;display:inline-block\">" +
                "Reset Password" +
                "</a>" +
                "</p>" +
                "<p>If the button doesn’t work, copy and paste this link into your browser:</p>" +
                "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>" +
                "<hr style=\"border:none;border-top:1px solid #eee;margin:22px 0\" />" +
                "<p style=\"color:#555\">For security, this link may expire soon. Your token is:</p>" +
                "<pre style=\"background:#f6f8fa;padding:10px;border-radius:6px\">" + token + "</pre>" +
                "<p>If you didn’t request this, you can safely ignore this email.</p>" +
                "<p>— APMIS Support</p>" +
                "</div>";
    }

    private static String buildText(String resetLink, String token) {
        return "Hello,\n\n" +
                "We received a request to reset your password.\n\n" +
                "Reset link: " + resetLink + "\n\n" +
                "Your token (if needed): " + token + "\n\n" +
                "If you didn’t request this, ignore this email.\n\n" +
                "— APMIS Support";
    }

}