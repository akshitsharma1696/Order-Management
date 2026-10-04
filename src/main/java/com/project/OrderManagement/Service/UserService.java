package com.project.OrderManagement.Service;

import com.project.OrderManagement.Entity.Product;
import com.project.OrderManagement.Entity.User;
import com.project.OrderManagement.ErrorMessage.ProductNotFoundException;
import com.project.OrderManagement.ErrorMessage.UserNotFoundException;
import com.project.OrderManagement.repository.UserRepository;
import com.project.OrderManagement.repository.productRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final productRepository productRepository;

    @Autowired
    public UserService(UserRepository userRepository, productRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public User createUser(User user) {                          // create username function
        User existingUser = userRepository.findByUsername(user.getUsername());
        if(existingUser != null){
            if(existingUser.getPassword().equals(user.getPassword())){
                throw new UserNotFoundException(("User already exists"));
            }
            else{
                throw new UserNotFoundException("Username already exists, Please change your username!!");
            }
        }
        else{
            user.setId(UUID.randomUUID());
        }
        return userRepository.save(user);
    }

    public List<User> createUsers(List<User> users) {       //create list of users
        for (User user : users) {
            user.setId(UUID.randomUUID()); // Generate a new UUID for each user
        }
        return userRepository.saveAll(users);
    }
    public User loginUser(String username, String password) {     //user successfully logged in or not
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new UserNotFoundException("Invalid username or password");
        }
        return user;
    }

    public List<User> getAllUsers() {    // showing the list of existting users
        return userRepository.findAll();
    }
    public Page<User> getAllUserswithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    public ResponseEntity<User> getUserByUuid(UUID id) {      //get user using generated id
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }
    public ResponseEntity<User> getUserByUsername(String username) {     // user by username
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    public ResponseEntity<User> addProductToUserOrder(UUID userId, UUID productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

        user.addOrderedProductId(productId.toString());
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
    public ResponseEntity<List<Product>> getOrderedProductsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        List<String> orderedProductIds = user.getOrderedProductIds();
        List<Product> orderedProducts = new ArrayList<>();
        for (String productId : orderedProductIds) {
            productRepository.findById(UUID.fromString(productId)).ifPresent(orderedProducts::add);
        }

        return ResponseEntity.ok(orderedProducts);
    }

    public ResponseEntity<User> updateUser(UUID userId, User updatedUser) {          //update info of existing user
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());

            // Save the updated user in the database
            userRepository.save(existingUser);

            return ResponseEntity.ok().body(existingUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> deleteUser(UUID userId) {
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isPresent()) {
            userRepository.deleteById(userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } else {
            // User not found, return a custom error response
            String errorMessage = "User with ID " + userId + " not found. Cannot delete user.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

}
