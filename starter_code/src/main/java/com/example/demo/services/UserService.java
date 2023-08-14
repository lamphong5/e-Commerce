package com.example.demo.services;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseEntity<User> findUserById(Long id);

    ResponseEntity<User> findUserByUserName(String username);

    ResponseEntity<User> createNewUser(CreateUserRequest createUserRequest);
}
