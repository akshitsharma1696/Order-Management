package com.project.OrderManagement;

import com.project.OrderManagement.Entity.Product;
import com.project.OrderManagement.Entity.User;
import com.project.OrderManagement.ErrorMessage.ProductNotFoundException;
import com.project.OrderManagement.ErrorMessage.UserNotFoundException;
import com.project.OrderManagement.Service.UserService;
import com.project.OrderManagement.repository.UserRepository;
import com.project.OrderManagement.repository.productRepository;
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

import java.util.*;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertNull;
import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private productRepository productRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void getAllUsersTest() {                         // get all user test case
        UUID userId = UUID.randomUUID();
        List<User> users = Arrays.asList(
                new User(userId, "Akriti", "12"),
                new User(userId, "Akshit", "33")
        );
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }
    @Test
    public void testGetAllUserseithPagination() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User(UUID.randomUUID(),"User 1", "password1"));
        userList.add(new User(UUID.randomUUID(),"User 2", "password2"));

        Page<User> userPage = new PageImpl<>(userList);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getAllUserswithPagination(pageable);
        assertEquals(userList.size(), result.getContent().size());

    }

    @Test
    void getUserByUsernameTestCase() {                    // get user by username testcase if user exists
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "11234");

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<User> result = userService.getUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody() != null;
        System.out.println(result.getBody());
        assert result.getBody().getId().equals(userId);
        assert result.getBody().getUsername().equals(username);
    }

    @Test
    void getUserByUsernameTestCase_UserNotFound() {          // get user by username and if user doesn't exists
        String username = "userNotExits";
        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<User> result = userService.getUserByUsername(username);
        verify(userRepository, times(1)).findByUsername(username);
        assert result.getStatusCode() == HttpStatus.NOT_FOUND;
        assert result.getBody() == null;
    }

    @Test
    void getUserByUuidTestCase() {                        // get user by Uuid if exits
        UUID userid = UUID.randomUUID();
        User user = new User(userid, "testcase", "345");
        when(userRepository.findById(userid)).thenReturn(Optional.of(user));

        ResponseEntity<User> result = userService.getUserByUuid(userid);

        verify(userRepository, times(1)).findById(userid);
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody().getId().equals(userid);
    }

    @Test
    void getUserByUuidTestCase_NotFound() {
        UUID userid = UUID.randomUUID();
        when(userRepository.findById(userid)).thenReturn(Optional.empty());

        ResponseEntity<User> result = userService.getUserByUuid((userid));

        verify(userRepository, times(1)).findById(userid);
        assert result.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert result.getBody() == null;
    }

    @Test
    void createUserTestCase_UsernameAvailable() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");

        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // Given
        String username = "existinguser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("newpassword");

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword("existingpassword");
        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testCreateUser_Successful() {
        // Given
        String username = "testuser";
        String password = "testpassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        // Then
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
    }

    @Test
    void testCreateUser_UserAlreadyExists_PasswordMatches() {
        // Given
        String existingUsername = "existinguser";
        String existingPassword = "existingpassword";
        User existingUser = new User();
        existingUser.setUsername(existingUsername);
        existingUser.setPassword(existingPassword);

        User newUser = new User();
        newUser.setUsername(existingUsername);
        newUser.setPassword(existingPassword);

        when(userRepository.findByUsername(existingUsername)).thenReturn(existingUser);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.createUser(newUser));
        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(existingUsername);
        verify(userRepository, never()).save(newUser);
    }

    @Test
    void testCreateUsers_Successful() {
        // Given
        List<User> usersToSave = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        usersToSave.add(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        usersToSave.add(user2);

        // When
        when(userRepository.saveAll(usersToSave)).thenReturn(usersToSave);

        List<User> savedUsers = userService.createUsers(usersToSave);

        // Then
        verify(userRepository, times(1)).saveAll(usersToSave);
        assertNotNull(savedUsers);
        assertEquals(usersToSave.size(), savedUsers.size());
        for (int i = 0; i < usersToSave.size(); i++) {
            User user = usersToSave.get(i);
            User savedUser = savedUsers.get(i);

            assertEquals(user.getUsername(), savedUser.getUsername());
            assertEquals(user.getPassword(), savedUser.getPassword());
        }
    }

    @Test
    void testLoginUser_Successful() {
        String username = "testuser";
        String password = "testpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        // When
        when(userRepository.findByUsername(username)).thenReturn(user);
        User loggedInUser = userService.loginUser(username, password);

        // Then
        verify(userRepository, times(1)).findByUsername(username);
        assertNotNull(loggedInUser);
        assertEquals(username, loggedInUser.getUsername());
        assertEquals(password, loggedInUser.getPassword());
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        String username = "testuser";
        String password = "testpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("wrongpassword");

        // When
        when(userRepository.findByUsername(username)).thenReturn(user);
        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.loginUser(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoginUser_UserNotFound() {

        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(null);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.loginUser(username, "password"));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testAddProductToUserOrder_Successful() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        Product product = new Product();
        product.setId(productId);
        product.setName("Product 1");
        product.setDescription("Description of Product 1");
        product.setPrice(19.99);
        product.setQuantity(10);
        product.setAddress("Address of Product 1");
        product.setUserId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<User> responseEntity = userService.addProductToUserOrder(userId, productId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, times(1)).findById(productId);
        verify(userRepository, times(1)).save(user);

        assertNotNull(responseEntity);
        assertEquals(user, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertTrue(user.getOrderedProductIds().contains(productId.toString()));
    }

    @Test
    void testAddProductToUserOrder_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.addProductToUserOrder(userId, productId));
        assertEquals("User with ID " + userId + " not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, never()).findById(productId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddProductToUserOrder_ProductNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> userService.addProductToUserOrder(userId, productId));
        assertEquals("Product with ID " + productId + " not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, times(1)).findById(productId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddProductToUser_Successful() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        Product product = new Product();
        product.setId(productId);
        product.setName("Product 1");
        product.setDescription("Description of Product 1");
        product.setPrice(19.99);
        product.setQuantity(10);
        product.setAddress("Address of Product 1");
        product.setUserId(UUID.randomUUID());

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<User> responseEntity = userService.addProductToUserOrder(userId, productId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, times(1)).findById(productId);
        verify(userRepository, times(1)).save(user);

        assertNotNull(responseEntity);
        assertEquals(user, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());

        // Ensure the product ID is added to the user's ordered product list
        assertTrue(user.getOrderedProductIds().contains(productId.toString()));
    }

    @Test
    void testGetOrderedProductsByUser_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getOrderedProductsByUser(userId));
        assertEquals("User with ID " + userId + " not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, never()).findById(any(UUID.class));
    }

    @Test
    void testGetOrderedProductsByUser_Successful() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPassword("testpassword");

        List<String> orderedProductIds = new ArrayList<>();
        orderedProductIds.add(UUID.randomUUID().toString());
        orderedProductIds.add(UUID.randomUUID().toString());
        user.setOrderedProductIds(orderedProductIds);

        Product product1 = new Product();
        product1.setId(UUID.fromString(orderedProductIds.get(0)));
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(UUID.fromString(orderedProductIds.get(1)));
        product2.setName("Product 2");

        List<Product> expectedOrderedProducts = new ArrayList<>();
        expectedOrderedProducts.add(product1);
        expectedOrderedProducts.add(product2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(UUID.fromString(orderedProductIds.get(0)))).thenReturn(Optional.of(product1));
        when(productRepository.findById(UUID.fromString(orderedProductIds.get(1)))).thenReturn(Optional.of(product2));

        ResponseEntity<List<Product>> responseEntity = userService.getOrderedProductsByUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, times(2)).findById(any(UUID.class));assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<Product> responseProducts = responseEntity.getBody();
        assertNotNull(responseProducts);
        assertEquals(expectedOrderedProducts.size(), responseProducts.size());

        for (int i = 0; i < expectedOrderedProducts.size(); i++) {
            Product expectedProduct = expectedOrderedProducts.get(i);
            Product actualProduct = responseProducts.get(i);

            assertEquals(expectedProduct.getId(), actualProduct.getId());
            assertEquals(expectedProduct.getName(), actualProduct.getName());
        }

    }
    @Test
    void testUpdateUser_Successful() {
        // Given
        UUID userId = UUID.randomUUID();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("newUsername");
        updatedUser.setPassword("newPassword");

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> responseEntity = userService.updateUser(userId, updatedUser);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        User updatedUserResponse = responseEntity.getBody();
        assertNotNull(updatedUserResponse);
        assertEquals(updatedUser.getUsername(), updatedUserResponse.getUsername());
        assertEquals(updatedUser.getPassword(), updatedUserResponse.getPassword());

    }
    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("newUsername");
        updatedUser.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ResponseEntity<User> responseEntity = userService.updateUser(userId, updatedUser);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }
    @Test
    void testDeleteUser_Successful() {
        UUID userId = UUID.randomUUID();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser");
        existingUser.setPassword("testpassword");

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> responseEntity = userService.deleteUser(userId);
        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User deleted successfully", responseEntity.getBody());
    }
    @Test
    void testDeleteUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(userId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + userId + " not found. Cannot delete user.", responseEntity.getBody());
    }

}
