package com.neb.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/**
 * Service responsible for sending application-related emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Sends a plain-text email to a specified recipient.
     *
     * @param to      Recipient email address
     * @param subject Subject of the email
     * @param text    Body content of the email
     */
    public void sendApplicationMail(String to, String subject, String text) 
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    
    // ✅ Send OTP for email verification
    public void sendOtpEmail(String to, String otp) {
        String subject = "Job Application OTP Verification";
        String text = "Dear Candidate,\n\nYour OTP for verification is: " + otp
                + "\nPlease enter this OTP to verify your email.\n\nThank you,\nNeb HR Team";
        sendApplicationMail(to, subject, text);
    }

    // ✅ Send confirmation after successful application
    public void sendConfirmationEmail(String to, String fullName, String jobTitle) {
        String subject = "Job Application Submitted Successfully";
        String text = "Dear " + fullName + ",\n\nYour application for the position '" + jobTitle
                + "' has been successfully submitted.\nOur HR team will review it and get back to you soon.\n\nBest Regards,\nNeb HR Team";
        sendApplicationMail(to, subject, text);
    }
}
