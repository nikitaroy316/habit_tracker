package com.nikita.habittracker.service;


import com.nikita.habittracker.exception.IllegalArgumentException;
import com.nikita.habittracker.exception.InformationExistException;
import com.nikita.habittracker.exception.InformationNotFoundException;
import com.nikita.habittracker.model.Role;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.request.LoginRequest;
import com.nikita.habittracker.repository.UserRepository;
import com.nikita.habittracker.security.JwtTokenUtil;
import com.nikita.habittracker.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WithMockUser
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    private User createSampleUser() {
        User user = new User("nikki", "nikki@example.com", Role.USER);
        user.setPassword("raw-password");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        Profile profile = new Profile();
        profile.setFirstName("Nikki");
        profile.setLastName("Sri");
        profile.setBio("Hello there");
        user.setProfile(profile);
        return user;
    }

    @Test
    public void test_createUser_success()
    {
        User user = createSampleUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        //assertNotNull(createdUser);
        assertEquals(user.getEmail(),createdUser.getEmail());
        assertEquals("encodedPassword",createdUser.getPassword());
        verify(userRepository).save(createdUser);
    }

    @Test
    public void test_createUser_emailExists_throwsException()
    {
        User existingUser = new User();
        existingUser.setEmail("priya@gmail.com");

        User newUser = new User();
        newUser.setEmail("priya@gmail.com");
        newUser.setPassword("rawPassword");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(existingUser);

        InformationExistException exception = assertThrows(InformationExistException.class, ()-> {
            userService.createUser(newUser);
        });

        assertEquals("user with email address priya@gmail.com already exist.",exception.getMessage());

        verify(userRepository,never()).save(any());
    }

    @Test
    public void test_createUser_nullPassword_throwsException()
    {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword(null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        assertEquals("Password cannot be blank",exception.getMessage());
        verify(userRepository,never()).save(any());
    }

    @Test
    public void test_createUser_nullEmail_throwsException() {
        User user = new User();
        user.setEmail(null);
        user.setPassword("123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        assertEquals("Email cannot be blank", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdateUserProfile_success() throws Exception
    {
        User existingUser = createSampleUser();
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(existingUser);

        User updatedUser = createSampleUser();
        updatedUser.getProfile().setFirstName("Updated");

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUserProfile(updatedUser);

        assertEquals("Updated",result.getProfile().getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUserProfile_noChanges_throwsException() throws Exception
    {
        User existingUser = createSampleUser();
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(existingUser);

        assertThrows(InformationExistException.class, ()-> userService.updateUserProfile(existingUser));
        verify(userRepository,never()).save(any());
    }

    @Test
    public void testUpdateUserProfile_userNotFound_throwsException() throws Exception
    {
        User user = createSampleUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        assertThrows(InformationNotFoundException.class, ()-> userService.updateUserProfile(user));
    }

    @Test
    public void testLoginUser_success() throws Exception
    {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailAddress("test@gmail.com");
        loginRequest.setPassword("123");

        Authentication authentication = mock(Authentication.class);
        MyUserDetails myUserDetails = mock(MyUserDetails.class);

        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getUsername()).thenReturn("test@gmail.com");
        when(jwtTokenUtil.generateToken("test@gmail.com")).thenReturn("jwt-token");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        Optional<String> result = userService.loginUser(loginRequest);

        assertTrue(result.isPresent());
        assertEquals("jwt-token",result.get());
    }

    @Test
    public void testLoginUser_disabledAccount_throwsException() throws Exception
    {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailAddress("test@gmail.com");
        loginRequest.setPassword("123");

        when(authenticationManager.authenticate(any())).thenThrow(new org.springframework.security.authentication.DisabledException("Account disabled"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest));

        assertEquals("Your account is disabled.",exception.getMessage());
    }

    @Test
    public void testLoginUser_lockedAccount_throwsException() throws Exception
    {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailAddress("test@gmail.com");
        loginRequest.setPassword("123");

        when(authenticationManager.authenticate(any())).thenThrow(new org.springframework.security.authentication.LockedException("Account Locked"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest));

        assertEquals("Your account is locked.",exception.getMessage());
    }

    @Test
    public void testLoginUser_badCredentials_throwsException() throws Exception
    {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailAddress("test@gmail.com");
        loginRequest.setPassword("wrong");

        when(authenticationManager.authenticate(any())).thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest));

        assertEquals("Invalid username or password.",exception.getMessage());
    }

    @Test
    public void testGetCurrentLoggedInUser_success() throws Exception
    {
       User user = createSampleUser();
       MyUserDetails myUserDetails = new MyUserDetails(user);

       Authentication auth = new UsernamePasswordAuthenticationToken(myUserDetails,null,myUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        User result = UserService.getCurrentLoggedInUser();

        assertEquals(user.getEmail(),result.getEmail());

        SecurityContextHolder.clearContext();
    }
}
