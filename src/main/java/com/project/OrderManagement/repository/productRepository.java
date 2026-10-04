package com.project.OrderManagement.repository;

import com.project.OrderManagement.Entity.Product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface productRepository extends MongoRepository<Product, UUID> {
    List<Product> findByUserId(UUID userId);
    //List<Product> findByUsername(String username);
}