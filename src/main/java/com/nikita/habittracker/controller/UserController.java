package com.nikita.habittracker.controller;

import com.nikita.habittracker.dto.ProfileDTO;
import com.nikita.habittracker.dto.UserRequestDTO;
import com.nikita.habittracker.dto.UserResponseDTO;
import com.nikita.habittracker.mapper.ProfileMapper;
import com.nikita.habittracker.mapper.UserMapper;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.model.request.LoginRequest;
import com.nikita.habittracker.model.response.LoginResponse;
import com.nikita.habittracker.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userDTO){
        User user = UserMapper.toEntity(userDTO);

        User savedUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(savedUser));


    }

    @PostMapping("/login") // cleaner path
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try
        {
            Optional<String> jwtToken = userService.loginUser(loginRequest);
            return ResponseEntity.ok(new LoginResponse(jwtToken.get()));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int id)
    {
        Optional<User> user = userService.getUser(id);
        if(user.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserMapper.toDto(user.get()));
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers()
    {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDto)
                .toList();
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



    @GetMapping(path="/profile") //http://localhost:8080/auth/users/profile/
    public ProfileDTO getUserProfile(){
        return ProfileMapper.toDTO(UserService.getCurrentLoggedInUser().getProfile());
    }

    @Transactional
    @PutMapping(path="/profile") //http://localhost:9009/auth/users/profile/
    public ProfileDTO updateUserProfile(@Valid @RequestBody ProfileDTO profileDTO){
        User user = UserService.getCurrentLoggedInUser();
        user.setProfile(ProfileMapper.toEntity(profileDTO));
        return ProfileMapper.toDTO(userService.updateUserProfile(user).getProfile());

    }

    @Transactional
    @PutMapping(path = "/status")
    public UserResponseDTO updateUserStatus(@Valid  @RequestBody UserRequestDTO userRequestDTO)
    {
        User user = UserService.getCurrentLoggedInUser();
        user.setEnabled(userRequestDTO.isEnabled());
        user.setAccountNonLocked(userRequestDTO.isAccountNonLocked());

        User updatedUser = userService.updateUserStatus(user);
        return UserMapper.toDto(updatedUser);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getCurrentUserStatus() {
        User user = UserService.getCurrentLoggedInUser(); // already in your controller
        Map<String, Boolean> status = new HashMap<>();
        status.put("enabled", user.isEnabled());
        status.put("accountNonLocked", user.isAccountNonLocked());
        return ResponseEntity.ok(status);
    }

}
