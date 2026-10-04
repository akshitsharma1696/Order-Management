package com.project.OrderManagement.Entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "Users")
public class User {
    @MongoId
    private UUID id;
    @Field
    private String username;
    @Field
    private String password;

    private List<String> orderedProductIds;

    public User() {
        this.orderedProductIds = new ArrayList<>();
    }

    public User(UUID id, String username, String Password) {
            this.id=id;
            this.username=username;
            this.password=Password;
    }

    public List<String> getOrderedProductIds() {
        return orderedProductIds;
    }
    public void setOrderedProductIds(List<String> orderedProductIds) {
        this.orderedProductIds = orderedProductIds;
    }
    public void addOrderedProductId(String productId) {
        this.orderedProductIds.add(productId);
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}