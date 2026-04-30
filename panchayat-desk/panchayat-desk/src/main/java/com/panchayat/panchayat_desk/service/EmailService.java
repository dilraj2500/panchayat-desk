package com.panchayat.panchayat_desk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email send failed: " + e.getMessage());
        }
    }

    public void sendComplaintNotification(String secretaryEmail, String complaintTitle) {
        String subject = "New Complaint Received — PanchayatDesk";
        String body = "A new complaint has been submitted: " + complaintTitle +
                "\n\nPlease login to PanchayatDesk to review and resolve it.";
        sendEmail(secretaryEmail, subject, body);
    }

    public void sendStatusUpdate(String residentEmail, String complaintTitle, String status) {
        String subject = "Complaint Status Updated — PanchayatDesk";
        String body = "Your complaint '" + complaintTitle + "' status has been updated to: " + status;
        sendEmail(residentEmail, subject, body);
    }
}