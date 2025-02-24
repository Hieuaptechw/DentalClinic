package com.dentalclinic.controllers;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailController {

    private static final String SENDER_EMAIL = "phihieu6789a@gmail.com";
    private static final String SENDER_PASSWORD = "vltq ftwo csge jmjn";
    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getAppointmentReminderTemplate(String patientName, String date, String time) {
        return "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 10px; max-width: 500px; margin: auto; background-color: #f9f9f9;'>"
                + "<h2 style='color: #2c3e50; text-align: center;'>📅 Dental Appointment Reminder</h2>"
                + "<p>Hello <strong>" + patientName + "</strong>,</p>"
                + "<p>You have an appointment with the dentist on:</p>"
                + "<p style='font-size: 16px;'><strong>🕒 Date: </strong>" + date + "</p>"
                + "<p style='font-size: 16px;'><strong>⏰ Time: </strong>" + time + "</p>"
                + "<p>Please arrive on time. If there are any changes, kindly contact us.</p>"
                + "<p style='color: #3498db; font-weight: bold;'>Thank you!</p>"
                + "</div>";
    }

    public static String getMissedAppointmentTemplate(String name, String date, String time) {
        return "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2 style='color: #C0392B;'>⚠️ Missed Appointment Notification</h2>"
                + "<p>Hello <b>" + name + "</b>,</p>"
                + "<p>We noticed that you missed your appointment at <b>" + time + "</b> on <b>" + date + "</b>.</p>"
                + "<p>If there was any reason, please contact us to reschedule as soon as possible.</p>"
                + "<br><p>📍 <i>DentalClinic</i></p>"
                + "</div>";
    }

}
