package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @Autowired
    private CartController cartController;

    @Autowired
    private UserController userController;

    private Authentication authentication;

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken("phongnhl", null, Collections.emptyList());
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("phongnhl");
        createUserRequest.setPassword("Pass.123");
        createUserRequest.setConfirmPassword("Pass.123");
    }

    @AfterEach
    void tearDown() {
        authentication = null;
        createUserRequest = null;
    }

    @Test
    void submitSuccess() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("phongnhl");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);

        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("phongnhl");
        Assertions.assertEquals(200, orderResponseEntity.getStatusCodeValue());
    }

    @Test
    void submitFailForbidden() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct3", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("dungdp");
        Assertions.assertEquals(403, orderResponseEntity.getStatusCodeValue());
    }

    @Test
    void submitFailUserNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("dungct");
        Assertions.assertEquals(404, orderResponseEntity.getStatusCodeValue());
    }

    @Test
    void getOrdersForUserSuccess() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("phongnhl");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);

        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("phongnhl");
        ResponseEntity<List<UserOrder>> listResponseEntity = orderController.getOrdersForUser("phongnhl");
        Assertions.assertEquals(200, listResponseEntity.getStatusCodeValue());
    }

    @Test
    void getOrdersForUserFailForbidden() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<List<UserOrder>> listResponseEntity = orderController.getOrdersForUser("dungdp");
        Assertions.assertEquals(403, listResponseEntity.getStatusCodeValue());
    }

    @Test
    void getOrdersForUserFailUserN() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<List<UserOrder>> listResponseEntity = orderController.getOrdersForUser("dungct");
        Assertions.assertEquals(404, listResponseEntity.getStatusCodeValue());
    }
}