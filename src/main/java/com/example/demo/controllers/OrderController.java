package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	private static final Logger log4jLogger = Logger.getLogger(OrderController.class.getName());
	private static final org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(OrderController.class);


	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log4jLogger.info("SUBMIT_ORDER_FAILURE=Username: "+username+" not exists!");
			slf4jLogger.info("SUBMIT_ORDER_FAILURE=Username: "+username+" not exists!");
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log4jLogger.info("SUBMIT_ORDER_SUCCESS="+username+"'s Order created successfully");
		slf4jLogger.info("SUBMIT_ORDER_SUCCESS="+username+"'s Order created successfully");
		//empty cart
		user.getCart().setItems(new LinkedList<Item>());
		user.getCart().setTotal(new BigDecimal(0));
		userRepository.save(user);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
