package com.graphbanking.userservice;

import com.graphbanking.userservice.model.User;
import com.graphbanking.userservice.repository.UserRepository;
import com.graphbanking.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        assertNotNull(userService);
        assertNotNull(userRepository);
    }

    @Test
    void testCreateUser() {
        // Test user creation
        User user = userService.createUser("Test User", "test@example.com", "password123");
        
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        
        // Verify password is encrypted
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void testFindUserByEmail() {
        // Create a user
        User createdUser = userService.createUser("John Doe", "john@example.com", "password123");
        
        // Find the user by email
        User foundUser = userService.getUserByEmail("john@example.com").orElse(null);
        
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        // Create a user
        User createdUser = userService.createUser("Jane Smith", "jane@example.com", "password123");
        
        // Update the user
        User updatedUser = userService.updateUser(createdUser.getId(), "Jane Updated", null);
        
        assertNotNull(updatedUser);
        assertEquals("Jane Updated", updatedUser.getName());
        assertEquals("jane@example.com", updatedUser.getEmail());
        assertTrue(updatedUser.getUpdatedAt().isAfter(updatedUser.getCreatedAt()));
    }

    @Test
    void testDeleteUser() {
        // Create a user
        User createdUser = userService.createUser("Delete Me", "delete@example.com", "password123");
        Long userId = createdUser.getId();
        
        // Delete the user
        userService.deleteUser(userId);
        
        // Verify user is deleted
        assertFalse(userService.getUserById(userId).isPresent());
    }

    @Test
    void testDuplicateEmailThrowsException() {
        // Create a user
        userService.createUser("User One", "duplicate@example.com", "password123");
        
        // Try to create another user with the same email
        assertThrows(RuntimeException.class, () -> {
            userService.createUser("User Two", "duplicate@example.com", "password456");
        });
    }
}
