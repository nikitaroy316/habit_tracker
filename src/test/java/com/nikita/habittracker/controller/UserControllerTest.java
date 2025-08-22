package com.nikita.habittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikita.habittracker.dto.ProfileDTO;
import com.nikita.habittracker.dto.UserRequestDTO;
import com.nikita.habittracker.exception.InformationExistException;
import com.nikita.habittracker.mapper.ProfileMapper;
import com.nikita.habittracker.mapper.UserMapper;
import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.Role;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.model.request.LoginRequest;
import com.nikita.habittracker.security.MyUserDetails;
import com.nikita.habittracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDTO getValidUserDTO() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserName("nikki");
        userRequestDTO.setPassword("123");
        userRequestDTO.setEmail("niki@gmail.com");
        userRequestDTO.setRole(Role.USER);

        ProfileDTO profile = new ProfileDTO();
        profile.setFirstName("nikki");
        profile.setLastName("sri");
        profile.setBio("Just getting started!");

        userRequestDTO.setProfile(profile);

        return userRequestDTO;
    }

    private LoginRequest getValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailAddress("user@example.com");
        loginRequest.setPassword("123456");
        return loginRequest;
    }

    private void setId(User user, int id) throws Exception {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
    }

    // covers all /register API Unit tests
    @Test
    public void testCreateUser_success() throws Exception {
        UserRequestDTO user = getValidUserDTO();

        User createdUser = new User();
        createdUser.setUserName(user.getUserName());
        createdUser.setEmail(user.getEmail());
        createdUser.setPassword("encodedPassword");

        when(userService.createUser(any())).thenReturn(createdUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(201));
    }

    @Test
    public void testCreateUser_missingEmail_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.setEmail(null);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    public void testCreateUser_invalidEmail_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.setEmail("invalidEmail");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email address"));
    }

    @Test
    public void testCreateUser_existingEmail_return409() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        when(userService.createUser(any())).thenThrow(new InformationExistException("user with email address " + user.getEmail() + " already exist."));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("user with email address " + user.getEmail() + " already exist."));
    }

    @Test
    public void testCreateUser_missingPassword_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.setPassword(null);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password is required"));
    }

    @Test
    public void testCreateUser_invalidPassword_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.setPassword("12");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be at least 3 characters"));
    }

    @Test
    void testCreateUser_missingUserName_returns400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.setUserName("");  // empty string triggers @NotBlank

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userName").value("Username is required"));
    }

    @Test
    public void testCreateUser_blankFirstName_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();

        user.getProfile().setFirstName("");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.['profile.firstName']").value("firstName cannot be blank"));
    }

    @Test
    public void testCreateUser_missingProfile_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();

        user.setProfile(null);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.profile").value("Profile is required"));
    }

    @Test
    public void testCreateUser_invalidRole_return400() throws Exception {
        String invalidRolePayload = """
                {
                  "userName": "testuser",
                  "email": "testuser@example.com",
                  "password": "1234",
                  "role": " ",  // Invalid role (not USER or ADMIN)
                  "profile": {
                    "firstName": "Test",
                    "lastName": "User",
                    "bio": "Testing invalid role"
                  }
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRolePayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid role value"));
    }

    @Test
    public void testCreateUser_missingRole_return400() throws Exception {
        UserRequestDTO user = getValidUserDTO();

        user.setRole(null);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.role").value("Role is required"));
    }

    @Test
    public void testCreateUser_emptyLastNameAndBio_400() throws Exception {
        UserRequestDTO user = getValidUserDTO();
        user.getProfile().setLastName("");
        user.getProfile().setBio("");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['profile.lastName']").value("lastName cannot be blank"))
                .andExpect(jsonPath("$.['profile.bio']").value("bio cannot be blank"));
    }

    @Test
    public void testCreateUser_emptyPayload_returns400() throws Exception {

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.profile").value("Profile is required"))
                .andExpect(jsonPath("$.userName").value("Username is required"))
                .andExpect(jsonPath("$.email").value("Email is required"))
                .andExpect(jsonPath("$.password").value("Password is required"))
                .andExpect(jsonPath("$.role").value("Role is required"));
    }

    //covers login user unit tests

    @Test
    public void testLoginUser_success() throws Exception {
        LoginRequest loginRequest = getValidLoginRequest();
        loginRequest.setEmailAddress("test@gmail.com");
        loginRequest.setPassword("123");

        when(userService.loginUser(any())).thenReturn(Optional.of("test-jwt-token"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("test-jwt-token"));

    }

    @Test
    public void testLoginUser_failure() throws Exception {
        LoginRequest loginRequest = getValidLoginRequest();

        when(userService.loginUser(any())).thenThrow(new RuntimeException("Invalid username or password."));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwt").value("Invalid username or password."));

    }

    @Test
    public void testLoginUser_missingEmailAndPassword_returns400() throws Exception {
        LoginRequest loginRequest = getValidLoginRequest();
        loginRequest.setPassword("");
        loginRequest.setEmailAddress("");

        when(userService.loginUser(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password is required"))
                .andExpect(jsonPath("$.emailAddress").value("Email is required"));
    }

    @Test
    public void testLoginUser_disabledAccount_returns401() throws Exception {
        LoginRequest loginRequest = getValidLoginRequest();
        when(userService.loginUser(any())).thenThrow(new RuntimeException("Your account is disabled."));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwt").value("Your account is disabled."));
    }

    @Test
    public void testLoginUser_lockedAccount_returns401() throws Exception {
        LoginRequest loginRequest = getValidLoginRequest();
        when(userService.loginUser(any())).thenThrow(new RuntimeException("Your account is locked."));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwt").value("Your account is locked."));
    }

   /* @Test
    public void testUpdateUserStatus_success() throws Exception
    {
        String validRolePayload = """
        {
          "enabled": true,
          "accountNonLocked": true
        }
        """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRolePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Invalid role value"));


    }*/

   /* @Test
    public void testUpdateUserStatus_success() throws Exception {
        // Prepare request DTO
        UserRequestDTO requestDTO = getValidUserDTO();
        requestDTO.setAccountNonLocked(true);
        requestDTO.setEnabled(true);

        User user = UserMapper.toEntity(requestDTO);

        // Set up custom user in security context
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                myUserDetails,
                null,
                myUserDetails.getAuthorities()
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        // Mock service behavior
        when(userService.updateUserStatus(any(User.class))).thenReturn(user);

        // Execute and verify
        mockMvc.perform(put("/api/users/status")  // <-- fixed here
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.accountNonLocked").value(true));
    }
*/

    //covers all get user by id units tests
    @Test
    public void testGetUserByID_success() throws Exception {
        int userID = 1;
        User user = UserMapper.toEntity(getValidUserDTO());
        setId(user, userID);
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        when(userService.getUser(userID)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userID))
                .andExpect(jsonPath("$.userName").value("nikki"))
                .andExpect(jsonPath("$.email").value("niki@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.accountNonLocked").value(true))
                .andExpect(jsonPath("$.profile.firstName").value("nikki"))
                .andExpect(jsonPath("$.profile.lastName").value("sri"))
                .andExpect(jsonPath("$.profile.bio").value("Just getting started!"));
    }

    @Test
    public void testGetUserByID_returns404() throws Exception {
        int userId = 999;

        when(userService.getUser(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllUsers_success() throws Exception {
        // Arrange
        User user1 = new User("nikki", "nikki@example.com", Role.USER);
        user1.setId(1);
        user1.setEnabled(true);
        user1.setAccountNonLocked(true);
        Profile profile1 = new Profile();
        profile1.setFirstName("Nikki");
        profile1.setLastName("Sri");
        profile1.setBio("Testing bio");
        user1.setProfile(profile1);

        User user2 = new User("alex", "alex@example.com", Role.ADMIN);
        user2.setId(2);
        user2.setEnabled(false);
        user2.setAccountNonLocked(false);
        Profile profile2 = new Profile();
        profile2.setFirstName("Alex");
        profile2.setLastName("Doe");
        profile2.setBio("Another bio");
        user2.setProfile(profile2);

        List<User> userList = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/api/users") // adjust base path if needed

                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userName").value("nikki"))
                .andExpect(jsonPath("$[0].profile.firstName").value("Nikki"))
                .andExpect(jsonPath("$[1].userName").value("alex"))
                .andExpect(jsonPath("$[1].profile.firstName").value("Alex"));
    }

    //covers delete user by ID unit tests
    @Test
    public void testDeleteUserById_success() throws Exception {
        int userID = 1;
        User user = UserMapper.toEntity(getValidUserDTO());
        setId(user, userID);

        when(userService.getUser(userID)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/{id}", userID))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userID);

    }

    @Test
    public void testDeleteUserById_returns404() throws Exception {
        int userID = 1;
        User user = UserMapper.toEntity(getValidUserDTO());
        setId(user, userID);

        when(userService.getUser(userID)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/{id}", userID))
                .andExpect(status().isNotFound());

        verify(userService, never()).deleteUser(anyInt());

    }

    //covers profile API unit tests
    @Test
    public void testGetUserProfile_success() throws Exception {
        User user = UserMapper.toEntity(getValidUserDTO());

        MyUserDetails myUserDetails = new MyUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);


        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("nikki"))
                .andExpect(jsonPath("$.lastName").value("sri"))
                .andExpect(jsonPath("$.bio").value("Just getting started!"));

        SecurityContextHolder.clearContext();
    }


    @Test
    public void testUpdateUserProfile_success() throws Exception {
        User user = UserMapper.toEntity(getValidUserDTO());

        MyUserDetails myUserDetails = new MyUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ProfileDTO updatedProfile = new ProfileDTO();
        updatedProfile.setFirstName("updatedFirstName");
        updatedProfile.setLastName("updateLastName");
        updatedProfile.setBio("updatedBio");

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setUserName(updatedUser.getUserName());
        updatedUser.setEmail(updatedUser.getEmail());
        updatedUser.setRole(updatedUser.getRole());
        updatedUser.setEnabled(true);
        updatedUser.setAccountNonLocked(true);
        updatedUser.setProfile(ProfileMapper.toEntity(updatedProfile));

        when(userService.updateUserProfile(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedProfile.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedProfile.getLastName()))
                .andExpect(jsonPath("$.bio").value(updatedProfile.getBio()));

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUpdateUserProfile_invalidProfile_returns400() throws Exception {
        // Arrange
        User user = UserMapper.toEntity(getValidUserDTO());
        user.setId(1);

        MyUserDetails myUserDetails = new MyUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ProfileDTO invalidProfile = new ProfileDTO();
        invalidProfile.setFirstName("");  // blank, assuming @NotBlank validation
        invalidProfile.setLastName("");
        invalidProfile.setBio("");

        // Act & Assert
        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProfile)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['firstName']").value("firstName cannot be blank"))  // adjust validation message as per your code
                .andExpect(jsonPath("$.['lastName']").value("lastName cannot be blank"))
                .andExpect(jsonPath("$.['bio']").value("bio cannot be blank"));

        SecurityContextHolder.clearContext();
    }

}
