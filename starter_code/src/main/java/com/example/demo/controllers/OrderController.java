package com.example.demo.controllers;

import java.util.List;
import java.util.Objects;

import com.example.demo.security.Authorization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import javax.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final Logger logger = LogManager.getLogger(OrderController.class);

	private UserRepository userRepository;
	
	private OrderRepository orderRepository;

	public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
	}

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		logger.info("submit - Start!!!");
		logger.info("submit - request get by username : username = {}", username);

		if (!Authorization.checkPersonalAccess(username)) {
			logger.error("submit - Not allowed!");
			logger.info("submit - End!!!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		User user = userRepository.findUserByUsername(username);
		if(ObjectUtils.isEmpty(user)) {
			logger.error("submit - User not found!");
			logger.info("submit - End!!!");
			return ResponseEntity.notFound().build();
		}
		Cart cart = user.getCart();
		cart.setUser(user);

		UserOrder order = UserOrder.createFromCart(cart);
		orderRepository.save(order);
		logger.info("submit - Success!");
		logger.info("submit - End!!!");
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		logger.info("getOrdersForUser - Start...");
		logger.info("getOrdersForUser - request payload: username = {}", username);
		if (!Authorization.checkPersonalAccess(username)) {
			logger.error("getOrdersForUser - Not allowed!");
			logger.info("getOrdersForUser - End!!!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		User user = userRepository.findUserByUsername(username);
		if(ObjectUtils.isEmpty(user)) {
			logger.error("getOrdersForUser - User not found!");
			logger.info("getOrdersForUser - End!!!");
			return ResponseEntity.notFound().build();
		}
		logger.info("getOrdersForUser - Success");
		logger.info("getOrdersForUser - End");
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
