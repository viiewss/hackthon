package com.graphbanking.userservice.resolver;

import com.graphbanking.userservice.model.User;
import com.graphbanking.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

@Controller
public class UserResolver {
    
    @Autowired
    private UserService userService;
    
    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }
    
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.getUserById(id).orElse(null);
    }
    
    @QueryMapping
    public User userByEmail(@Argument String email) {
        return userService.getUserByEmail(email).orElse(null);
    }
    
    @MutationMapping
    public User createUser(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String email = (String) input.get("email");
        String password = (String) input.get("password");
        
        return userService.createUser(name, email, password);
    }
    
    @MutationMapping
    public User updateUser(@Argument Long id, @Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String email = (String) input.get("email");
        
        return userService.updateUser(id, name, email);
    }
    
    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        try {
            userService.deleteUser(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 