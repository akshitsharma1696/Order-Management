package com.project.OrderManagement.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "Products")
public class Product {
    @Id
    private UUID id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String address;
    private UUID userId;

    //private List<String> orderedByUserIds; // Store the IDs of users who ordered this product


    // Default constructor
//    public Product() {
//        this.orderedByUserIds = new ArrayList<>();
//    }

    // Parameterized constructor
    public Product(UUID id, String name, String description, double price, int quantity,String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.address = address;
    }

    public Product() {

    }

//    public List<String> getOrderedByUserIds() {
//        return orderedByUserIds;
//    }
//
//    public void addOrderedByUserId(String userId) {
//        this.orderedByUserIds.add(userId);
//    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

