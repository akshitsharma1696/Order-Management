package com.project.OrderManagement.controller;

import com.project.OrderManagement.Entity.Product;
import com.project.OrderManagement.Entity.User;
import com.project.OrderManagement.Service.ProductService;
import com.project.OrderManagement.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private ProductService productService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    @PostMapping("/createUsers")
    public ResponseEntity<List<User>> createUsers(@RequestBody List<User> users) {
        List<User> createdUsers = userService.createUsers(users);
        return ResponseEntity.ok(createdUsers);
    }

    @PostMapping("/loginUser")
    public ResponseEntity<User> loginUser(@RequestParam String username,@RequestParam String password){
        User user= userService.loginUser(username,password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/getUserwithPagination")
    public ResponseEntity<Page<User>> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> UsersPage = userService.getAllUserswithPagination(pageable);
        return ResponseEntity.ok(UsersPage);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<User> getUserByUuid(@PathVariable UUID id) {
        return userService.getUserByUuid(id);
    }
    @PostMapping("/{userId}/products/{productId}")
    public ResponseEntity<User> addProductToUserOrder(@PathVariable UUID userId, @PathVariable UUID productId) {
        return userService.addProductToUserOrder(userId, productId);
    }

    @GetMapping("/{userId}/ordered-products")
    public ResponseEntity<List<Product>> getOrderedProductsByUser(@PathVariable UUID userId) {
        return userService.getOrderedProductsByUser(userId);
    }
    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }


    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody User updatedUser) {
        ResponseEntity<User> response = userService.updateUser(userId, updatedUser);
        if (response.getBody() == null) {
            // User not found
            String errorMessage = "User with ID " + userId + " not found. Cannot update user.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } else {
            return response;
        }
    }

    @DeleteMapping("deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        return userService.deleteUser(userId);
    }


}

