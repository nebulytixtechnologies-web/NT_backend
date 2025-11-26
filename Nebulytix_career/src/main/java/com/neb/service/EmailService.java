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
        String text = 
                "Dear " + fullName + ",\n\n" +
                "Thank you for applying for the position of " + jobTitle + " at Nebulytix Technologies.\n\n" + 
                "We are pleased to inform you that your application has been successfully received by our recruitment team. " +
                "Our hiring specialists will carefully review your resume and evaluate your profile against the role requirements.\n\n" +

                "If your qualifications match our current needs, we will reach out to you for the next steps, which may include:\n" +
                "• Initial HR screening\n" +
                "• Technical assessment or assignment\n" +
                "• Technical interview with our engineering panel\n" +
                "• Final discussion with the management team\n\n" +

                "Please note that this process may take a few days depending on the volume of applications. " +
                "We appreciate your patience and interest in joining our organization.\n\n" +

                "In the meantime, feel free to explore more about our culture, technologies, and ongoing projects on our website " +
                "and social media pages.\n\n" +

                "If you have any questions, you may reply to this email or contact our HR support team.\n\n" +

                "We wish you the very best in the selection process.\n\n" +
                "Warm Regards,\n" +
                "HR Team,\n" +
                "Nebulytix Technologies";

        sendApplicationMail(to, subject, text);
    }
}
