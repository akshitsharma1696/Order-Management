package com.project.OrderManagement;


import com.project.OrderManagement.Entity.Product;
import com.project.OrderManagement.ErrorMessage.ProductNotFoundException;
import com.project.OrderManagement.Service.ProductService;
import com.project.OrderManagement.repository.productRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond.when;
import org.springframework.data.domain.Page;

@SpringBootTest
public class ProductServiceTests {
    @Mock
    private productRepository productRepository;
    @Mock
    Page<Product> productPage = Mockito.mock(Page.class);
    @InjectMocks
    private ProductService productService;

    public ProductServiceTests() {
    }

    @Test
    public void getAllProductsTest() {
        UUID productId = UUID.randomUUID();
        List<Product> products = Arrays.asList(new Product(productId, "product1", "product desc", 2333.0, 1, "Himachal"), new Product(productId, "product2", "product desc", 8333.0, 2, "Banglore"));
        Mockito.when(this.productRepository.findAll()).thenReturn(products);
        List<Product> result = this.productService.getAllProducts();
        assertEquals(2, result.size());
    }
    @Test
    public void testGetAllProducts() {
        // Mock data
        List<Product> productList = new ArrayList<>();
        productList.add(new Product( UUID.randomUUID(),"Product 1", "Description 1", 19.99, 10, "Address 1"));
        productList.add(new Product(UUID.randomUUID(),"Product 2", "Description 2", 24.99, 5, "Address 2"));

        Page<Product> productPage = new PageImpl<>(productList);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getAllProductswithPagination(pageable);
        assertEquals(productList.size(), result.getContent().size());

    }


        @Test
    void getProductByIdTestCase() {
        UUID productId = UUID.randomUUID();
        String productName = "Test Product";
        Product product = new Product(productId, productName, "Description", 29.99, 10, "Address");
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product result = this.productService.getProductById(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
        com.mongodb.assertions.Assertions.assertNotNull(result);
        assertEquals(productName, result.getName());
    }

    @Test
    void getProductByIdTestCase_NotFound() {
        UUID productId = UUID.randomUUID();
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            this.productService.getProductById(productId);
        });
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
    }

    @Test
    void testAddProduct() {
        UUID userId = UUID.randomUUID();
        Product productToAdd = new Product();
        productToAdd.setName("New Product");
        productToAdd.setDescription("This is a new product.");
        productToAdd.setPrice(29.99);
        productToAdd.setQuantity(10);
        productToAdd.setAddress("Himachal");
        productToAdd.setUserId(userId);
        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setName("New Product");
        savedProduct.setDescription("This is a new product.");
        savedProduct.setPrice(29.99);
        savedProduct.setQuantity(10);
        savedProduct.setAddress("banglore");
        savedProduct.setUserId(userId);
        Mockito.when((Product)this.productRepository.save((Product)Mockito.any(Product.class))).thenReturn(savedProduct);
        Product addedProduct = this.productService.addProduct(productToAdd, userId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).save((Product)Mockito.any(Product.class));
        com.mongodb.assertions.Assertions.assertNotNull(addedProduct);
        assertEquals(savedProduct.getId(), addedProduct.getId());
    }

    @Test
    void testAddProducts() {
        List<Product> productsToAdd = new ArrayList();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description of Product 1");
        product1.setPrice(19.99);
        product1.setQuantity(10);
        product1.setAddress("Address of Product 1");
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description of Product 2");
        product2.setPrice(24.99);
        product2.setQuantity(5);
        product2.setAddress("Address of Product 2");
        productsToAdd.add(product1);
        productsToAdd.add(product2);
        Mockito.when(this.productRepository.saveAll(productsToAdd)).thenReturn(productsToAdd);
        ResponseEntity<List<Product>> response = this.productService.addProducts(productsToAdd);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).saveAll(productsToAdd);
        com.mongodb.assertions.Assertions.assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Product> savedProducts = (List)response.getBody();
        com.mongodb.assertions.Assertions.assertNotNull(savedProducts);
        assertEquals(2, savedProducts.size());
        Iterator var6 = savedProducts.iterator();

        while(var6.hasNext()) {
            Product savedProduct = (Product)var6.next();
            com.mongodb.assertions.Assertions.assertNotNull(savedProduct.getId());
        }

    }

    @Test
    void testUpdateProduct() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(19.99);
        existingProduct.setQuantity(5);
        existingProduct.setAddress("Old Address");
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("New Product");
        updatedProduct.setDescription("New Description");
        updatedProduct.setPrice(24.99);
        updatedProduct.setQuantity(10);
        updatedProduct.setAddress("New Address");
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        Mockito.when((Product)this.productRepository.save((Product)Mockito.any(Product.class))).thenReturn(updatedProduct);
        Product updatedProductResponse = this.productService.updateProduct(productId, updatedProduct);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).save((Product)Mockito.any(Product.class));
        com.mongodb.assertions.Assertions.assertNotNull(updatedProductResponse);
        assertEquals(updatedProduct.getName(), updatedProductResponse.getName());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        UUID productId = UUID.randomUUID();
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("New Product");
        updatedProduct.setDescription("New Description");
        updatedProduct.setPrice(24.99);
        updatedProduct.setQuantity(10);
        updatedProduct.setAddress("New Address");
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.empty());
        Product updatedProductResponse = this.productService.updateProduct(productId, updatedProduct);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.never())).save((Product)Mockito.any(Product.class));
        com.mongodb.assertions.Assertions.assertNull(updatedProductResponse);
    }

    @Test
    void testDeleteProduct() {
        UUID productId = UUID.randomUUID();
        Product productToDelete = new Product();
        productToDelete.setId(productId);
        productToDelete.setName("New Product");
        productToDelete.setDescription("New Description");
        productToDelete.setPrice(24.99);
        productToDelete.setQuantity(10);
        productToDelete.setAddress("New Address");
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.of(productToDelete));
        this.productService.deleteProduct(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).delete(productToDelete);
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        UUID productId = UUID.randomUUID();
        Mockito.when(this.productRepository.findById(productId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            this.productService.deleteProduct(productId);
        });
        ((productRepository)Mockito.verify(this.productRepository, Mockito.times(1))).findById(productId);
        ((productRepository)Mockito.verify(this.productRepository, Mockito.never())).delete((Product)Mockito.any(Product.class));
    }
}
