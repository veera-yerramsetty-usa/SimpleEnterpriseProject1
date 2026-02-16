package org.sample.sampleenterpriseproj1.service;

import org.sample.sampleenterpriseproj1.model.User;
import org.sample.sampleenterpriseproj1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        user.setId(UUID.randomUUID().toString());
        user.setTime(Instant.now());
        user.setCreatedAt(Instant.now().toString());
        user.setUpdatedAt(Instant.now().toString());
        userRepository.save(user);
        return user;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .filter(u -> u.getVersion() >= 0)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getVersion() >= 0)
                .toList();
    }

    public User updateUser(String id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());
        user.setTime(Instant.now());
        user.setUpdatedAt(Instant.now().toString());
        userRepository.save(user);
        return user;
    }

    public void deleteUser(String id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}
