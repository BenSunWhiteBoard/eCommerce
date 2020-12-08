package com.example.demo.controllers;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log4jLogger = Logger.getLogger(UserController.class.getName());
    private static final org.slf4j.Logger slf4jLogger = LoggerFactory
            .getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        Cart cart = new Cart();
        user.setUsername(createUserRequest.getUsername());
        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log4jLogger.info("USER_CREATE_Failure=Create user with name " + createUserRequest.getUsername()
                    + " failed! Wrong password Format!");
            slf4jLogger.info("USER_CREATE_Failure=Create user with name " + createUserRequest.getUsername()
                    + " failed! Wrong password Format!");
            return ResponseEntity.badRequest().build();
        } else if (userRepository.findByUsername(user.getUsername()) != null) {
            log4jLogger.info("USER_CREATE_Failure=Username: " + createUserRequest.getUsername()
                    + " already exists! Try another!");
            slf4jLogger.info("USER_CREATE_Failure=Username: " + createUserRequest.getUsername()
                    + " already exists! Try another!");
            return ResponseEntity.badRequest().build();
        }
        cartRepository.save(cart);
        user.setCart(cart);
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        log4jLogger
                .info("USER_CREATE_SUCCESS=Create user with name " + createUserRequest.getUsername()
                        + " success!");
        slf4jLogger
                .info("USER_CREATE_SUCCESS=Create user with name " + createUserRequest.getUsername()
                        + " success!");
        return ResponseEntity.ok(user);
    }
}
