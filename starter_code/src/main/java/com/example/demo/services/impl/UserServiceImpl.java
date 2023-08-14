package com.example.demo.services.impl;

import com.example.demo.model.persistence.Cart;
import com.example.demo.security.Authorization;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepo;

    private final CartRepository cartRepo;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepo, CartRepository cartRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public ResponseEntity<User> findUserById(Long id) {
        logger.info("findUserById - Start...");
        logger.info("findUserById - request payload: id = {}", id);
        Optional<User> optionalUser = userRepo.findById(id);
        if (!optionalUser.isPresent()) {
            logger.error("findUserById - Not found");
            logger.info("findUserById - End");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String username = optionalUser.get().getUsername();
        if (!Authorization.checkPersonalAccess(username)) {
            logger.error("findUserById - Not allowed");
            logger.info("findUserById - End");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        logger.info("findUserById - Success");
        logger.info("findUserById - End");
        return ResponseEntity.ok(optionalUser.get());
    }

    @Override
    public ResponseEntity<User> findUserByUserName(String username) {
        logger.info("findUserByUserName - Start...");
        logger.info("findUserByUserName - request payload: username = {}", username);

        if (!Authorization.checkPersonalAccess(username)) {
            logger.error("findUserByUserName - Not allow");
            logger.info("findUserByUserName - End");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userRepo.findUserByUsername(username);
        if (Objects.isNull(user)) {
            logger.error("findUserByUserName - Not found");
            logger.info("findUserByUserName - End");
            return ResponseEntity.notFound().build();
        }

        logger.info("findUserByUserName - Success");
        logger.info("findUserByUserName - End");
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> createNewUser(CreateUserRequest createUserRequest) {
        logger.info("createNewUser - Start...");
        try {
            logger.info("createNewUser - request payload: createUserRequest = {}", new ObjectMapper().writer().writeValueAsString(createUserRequest));
        } catch (JsonProcessingException e) {
            logger.error("createNewUser - Server error");
            logger.info("createNewUser - End");
            throw new RuntimeException(e);
        }
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepo.save(cart);
        user.setCart(cart);

        if (createUserRequest.getPassword().length() <= 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.error("createNewUser - Bad request");
            logger.info("createNewUser - End");
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepo.save(user);
        logger.info("createNewUser - Success");
        logger.info("createNewUser - End");
        return ResponseEntity.ok(user);
    }
}
