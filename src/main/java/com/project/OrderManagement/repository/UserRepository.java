package com.project.OrderManagement.repository;

import com.project.OrderManagement.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends MongoRepository<User, UUID> {
    User findByUsername(String username);
    Optional<User> findById(UUID uuid);

}
