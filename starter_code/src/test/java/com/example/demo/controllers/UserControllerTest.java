package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Objects;
@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void createUserSuccess() {
        ResponseEntity<User> responseEntity = userController.createUser(createNewUserRequest("phongnhl", "Pass.123"));
        Assertions.assertEquals("phongnhl", Objects.requireNonNull(responseEntity.getBody()).getUsername());
    }

    @Test
    public void createUserFailInvalidPassword() {
        ResponseEntity<User> responseEntity = userController.createUser(createNewUserRequest("phongnhl1", "123"));
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findUserByIdSuccess() {
        ResponseEntity<User> createdUserResponse = userController.createUser(createNewUserRequest("phongnhl2", "Pass.123"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("phongnhl2", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findById(Objects.requireNonNull(createdUserResponse.getBody()).getId());
        Assertions.assertEquals("phongnhl2", Objects.requireNonNull(responseEntity.getBody()).getUsername());
    }

    @Test
    public void findUserByIdFailNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("phongnhl2", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findById(-1L);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findUserByIdFailForbidden() {
        ResponseEntity<User> createdUserResponse = userController.createUser(createNewUserRequest("phongnhl3", "Pass.123"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("dungdp", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findById(Objects.requireNonNull(createdUserResponse.getBody()).getId());
        Assertions.assertEquals(403, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findByUsernameSuccess() {
        ResponseEntity<User> createdUserResponse = userController.createUser(createNewUserRequest("phongnhl4", "Pass.123"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("phongnhl4", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findByUserName("phongnhl4");
        Assertions.assertEquals("phongnhl4", Objects.requireNonNull(responseEntity.getBody()).getUsername());
    }

    @Test
    public void findByUsernameFailForbidden() {
        ResponseEntity<User> createdUserResponse = userController.createUser(createNewUserRequest("phongnhl5", "Pass.123"));

        Authentication authentication = new UsernamePasswordAuthenticationToken("phongnhl4", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findByUserName("phongnhl5");
        Assertions.assertEquals(403, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findByUsernameFailNotFound() {
        ResponseEntity<User> createdUserResponse = userController.createUser(createNewUserRequest("phongnhl6", "Pass.123"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("phongnhl4", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<User> responseEntity = userController.findByUserName("phongnhl4");
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private CreateUserRequest createNewUserRequest(String username, String password){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);
        return createUserRequest;
    }
}