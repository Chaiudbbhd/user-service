package com.lpk.userservice;

import com.lpk.userservice.model.User;
import com.lpk.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user = new User("testuser", "testuser@gmail.com", "test123");
            userRepository.save(user);
            System.out.println("Inserted initial user into DB");
        }
    }
}
