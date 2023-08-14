package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.transaction.Transactional;
import java.util.Collections;

@Transactional
@SpringBootTest
class CartControllerTest {
    @Autowired
    private UserController userController;

    @Autowired
    private CartController cartController;

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
    void addToCartSuccess() {

        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("phongnhl");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyRequest);

        Assertions.assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void addToCartFailForbidden() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("dungdp");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyRequest);

        Assertions.assertEquals(403, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void addToCartFailItemNotFound() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("phongnhl");
        modifyRequest.setItemId(-1);
        modifyRequest.setQuantity(15);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyRequest);

        Assertions.assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void addToCartFailUserNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct3", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("dungct3");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyRequest);

        Assertions.assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void removeFromCartSuccess() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("phongnhl");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        cartController.addToCart(modifyRequest);

        ModifyCartRequest modifyRequestUpdate = new ModifyCartRequest();
        modifyRequestUpdate.setUsername("phongnhl");
        modifyRequestUpdate.setItemId(1);
        modifyRequestUpdate.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyRequestUpdate);

        Assertions.assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void removeFromCartFailForbidden() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("phongnhl");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        cartController.addToCart(modifyRequest);

        ModifyCartRequest modifyRequestUpdate = new ModifyCartRequest();
        modifyRequestUpdate.setUsername("dungct3");
        modifyRequestUpdate.setItemId(1);
        modifyRequestUpdate.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyRequestUpdate);

        Assertions.assertEquals(403, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void removeFromCartFailUserNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dungct3", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ModifyCartRequest modifyRequestUpdate = new ModifyCartRequest();
        modifyRequestUpdate.setUsername("dungct3");
        modifyRequestUpdate.setItemId(1);
        modifyRequestUpdate.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyRequestUpdate);

        Assertions.assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    void removeFromCartFailItemNotFound() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("phongnhl");
        modifyRequest.setItemId(1);
        modifyRequest.setQuantity(15);
        cartController.addToCart(modifyRequest);

        ModifyCartRequest modifyRequestUpdate = new ModifyCartRequest();
        modifyRequestUpdate.setUsername("phongnhl");
        modifyRequestUpdate.setItemId(-1);
        modifyRequestUpdate.setQuantity(5);
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyRequestUpdate);

        Assertions.assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }
}