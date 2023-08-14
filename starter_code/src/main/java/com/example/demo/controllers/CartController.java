package com.example.demo.controllers;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.security.Authorization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import javax.transaction.Transactional;


@RestController
@RequestMapping("/api/cart")
public class CartController {
	private final Logger logger = LogManager.getLogger(CartController.class);
	
	private UserRepository userRepository;
	
	private CartRepository cartRepository;
	
	private ItemRepository itemRepository;

	public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.itemRepository = itemRepository;
	}

	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
		logger.info("addToCart: Start!!!");
		try {
			logger.info("addToCart: request payload {}", new ObjectMapper().writer().writeValueAsString(request));
		} catch (JsonProcessingException e) {
			logger.error("addToCart: Server error!");
			logger.info("addToCart: End!!!");
			throw new RuntimeException(e);
		}
		ResponseEntity<Cart> responseEntity;
		if (!Authorization.checkPersonalAccess(request.getUsername())) {
			logger.error("addToCart: User Forbidden!");
			logger.info("addToCart: End!!!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		User user = userRepository.findUserByUsername(request.getUsername());

		if (Objects.isNull(user)) {
			logger.error("addToCart: User not found!");
			logger.info("addToCart: End!!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error("addToCart: Item not found!");
			logger.info("addToCart: End!!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		logger.info("addToCart: Success!!!");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		logger.info("removeFromCart: Start!!!");
		try {
			logger.info("removeFromCart: request payload {}", new ObjectMapper().writer().writeValueAsString(request));
		} catch (JsonProcessingException e) {
			logger.error("removeFromCart: Server error!!!");
			logger.info("removeFromCart: End!!!");
			throw new RuntimeException(e);
		}
		if (!Authorization.checkPersonalAccess(request.getUsername())) {
			logger.error("removeFromCart: Forbidden!");
			logger.info("removeFromCart: End!!!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		User user = userRepository.findUserByUsername(request.getUsername());
		if (Objects.isNull(user)) {
			logger.error("removeFromCart: User not found!");
			logger.info("removeFromCart: End!!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error("removeFromCart: Item not found!");
			logger.info("removeFromCart: End!!!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		return ResponseEntity.ok(cart);
	}
		
}
