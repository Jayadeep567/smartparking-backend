package com.parking;

import com.parking.model.*;
import com.parking.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ParkingApplication {
    public static void main(String[] args) { SpringApplication.run(ParkingApplication.class, args); }

    // Seeds a default admin and 12 parking slots on first start
    @Bean
    CommandLineRunner seed(UserRepository users, SlotRepository slots, PasswordEncoder enc) {
        return args -> {
            if (users.findByEmail("admin@parking.com").isEmpty())
                users.save(new User("Admin", "admin@parking.com", enc.encode("admin123"), Role.ADMIN));
            if (slots.count() == 0)
                for (int i = 1; i <= 12; i++) slots.save(new Slot("A-" + String.format("%02d", i)));
        };
    }
}
