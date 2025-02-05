package com.javaweb.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.entity.User;
import com.javaweb.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email đã tồn tại");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        int otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        sendOtpEmail(email, otp);
    }

    private int generateOtp() {
        return new Random().nextInt(900000) + 100000; // OTP 6 chữ số
    }

    private void sendOtpEmail(String email, int otp) {
        // Tích hợp JavaMailSender hoặc gửi email qua API bên thứ ba

    	EmailService.sendOTP(email, String.valueOf(otp));
        System.out.println("OTP đã gửi tới email " + email + ": " + otp);
    }
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean updateStatusByEmail(String email) {
        // Tìm user theo email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Cập nhật trạng thái
            user.setStatus(1);
            // Lưu lại trong database
            userRepository.save(user);
            return true; 
        }
        return false; 
    }


}
