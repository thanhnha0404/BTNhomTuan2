package com.javaweb.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    public static boolean sendOTP(String toEmail, String otp) {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP của Gmail
        properties.put("mail.smtp.port", "587"); // Cổng TLS

        // Tài khoản Gmail gửi OTP
        String myAccountEmail = "phamhoanghuy.2000vn@gmail.com";
        String password = "xxvb pzff upvb rshu"; // Mật khẩu ứng dụng (App Password)

        // Xác thực tài khoản Gmail
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        try {
            // Tạo thông điệp email
            Message message = prepareMessage(session, myAccountEmail, toEmail, otp);

            // Gửi email
            if (message != null) {
                Transport.send(message);
                System.out.println("OTP đã được gửi thành công đến email: " + toEmail);
                return true;
            }
        } catch (MessagingException e) {
            System.err.println("Có lỗi khi gửi email: " + e.getMessage());
        }
        return false;
    }

    private static Message prepareMessage(Session session, String fromEmail, String toEmail, String otp) {
        if (isValidEmail(toEmail)) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail)); // Địa chỉ email người gửi
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // Địa chỉ email người nhận
                message.setSubject("Your OTP Verification Code"); // Tiêu đề email
                message.setText("Hello,\n\nYour OTP code is: " + otp + "\n\nThank you!"); // Nội dung email
                return message;
            } catch (MessagingException e) {
                System.err.println("Error while preparing the email: " + e.getMessage());
            }
        }
        return null;
    }

    // Kiểm tra tính hợp lệ của địa chỉ email
    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
