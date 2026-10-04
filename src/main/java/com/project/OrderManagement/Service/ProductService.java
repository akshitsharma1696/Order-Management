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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final productRepository productRepository;
    private final UserRepository userRepository;
    @Autowired
    public ProductService(productRepository productRepository,UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository=userRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Page<Product> getAllProductswithPagination(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public Product addProduct(Product product,UUID userId) {
        product.setId(UUID.randomUUID());
        product.setUserId(userId);
        return productRepository.save(product);
    }

    public ResponseEntity<List<Product>> addProducts(List<Product> products) {
        for (Product product : products) {
            product.setId(UUID.randomUUID()); // Generate a UUID for each product
        }

        List<Product> savedProducts = productRepository.saveAll(products);
        return ResponseEntity.ok(savedProducts);
    }

    public Product updateProduct(UUID id, Product updatedProduct) {
        try {
            Product existingProduct = getProductById(id);
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setAddress(updatedProduct.getAddress());
            return productRepository.save(existingProduct);
        } catch (ProductNotFoundException ex) {
            return null;
        }
    }

    public void deleteProduct(UUID id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

}
