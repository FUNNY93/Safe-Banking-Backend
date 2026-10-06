package com.safebank.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;



@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    public void sendStatement(
            String email,
            byte[] pdf,
            String password) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(email);

            helper.setSubject(
                    "SAFE BANK - Transaction Statement"
            );

            helper.setText(
                    "Dear Customer,\n\n"
                    + "Please find your SAFE BANK transaction "
                    + "statement attached.\n\n"
                    + "PDF Password: "
                    + password
                    + "\n\n"
                    + "For security, please do not share "
                    + "your statement or password with anyone.\n\n"
                    + "Regards,\n"
                    + "SAFE BANK"
            );

            helper.addAttachment(
                    "SAFE-Bank-Statement.pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send statement email",
                    e
            );
        }
    }

    public void sendOtp(String email, String otp) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("SAFE BANK - Login OTP");

        message.setText(
                "Your SAFE BANK login OTP is: "
                + otp
                + "\n\nThis OTP is valid for 5 minutes."
                + "\n\nDo not share this OTP with anyone."
        );

        mailSender.send(message);
    }
}
