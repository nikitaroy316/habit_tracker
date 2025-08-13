package com.nikita.habittracker.controller;

import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.model.request.LoginRequest;
import com.nikita.habittracker.model.response.LoginResponse;
import com.nikita.habittracker.security.MyUserDetails;
import com.nikita.habittracker.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*@Transactional
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        return ResponseEntity.ok(userService.saveUser(user));
    }*/

    @PostMapping(path = "/register")
    public User createUser(@Valid @RequestBody User user){
        return userService.createUser(user);
    }

    @PostMapping("/login") // cleaner path
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<String> jwtToken = userService.loginUser(loginRequest);
        if (jwtToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Authentication failed!"));
        }
        return ResponseEntity.ok(new LoginResponse(jwtToken.get()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id)
    {
        Optional<User> user = userService.getUser(id);
        if(user.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user.get());
    }

    @GetMapping
    public List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id)
    {
        Optional<User> user = userService.getUser(id);
        if(user.isEmpty())
            return ResponseEntity.notFound().build();

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public static User getCurrentLoggedInUser(){
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder //After jwt is generated, Security Context Holder is created to hold the user's state
                .getContext().getAuthentication().getPrincipal(); // the entire User object, with authentication details
        return userDetails.getUser();
    }

    @GetMapping(path="/profile") //http://localhost:8080/auth/users/profile/
    public Profile getUserProfile(){
        return getCurrentLoggedInUser().getProfile();
    }

    @PutMapping(path="/profile") //http://localhost:9009/auth/users/profile/
    public Profile updateUserProfile(@Valid @RequestBody Profile profile){
        User user = getCurrentLoggedInUser();
        user.setProfile(profile);
        return userService.updateUserProfile(user).getProfile();
    }



}
