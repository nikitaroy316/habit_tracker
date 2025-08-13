package com.nikita.habittracker.service;

import com.nikita.habittracker.exception.InformationExistException;
import com.nikita.habittracker.exception.InformationNotFoundException;
import com.nikita.habittracker.model.Profile;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.model.request.LoginRequest;
import com.nikita.habittracker.repository.UserRepository;
import com.nikita.habittracker.security.JwtTokenUtil;

import com.nikita.habittracker.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }


    public User createUser(User user){
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(user.getEmail())); //checks if email address already exists in database
        if (userOptional.isEmpty()){ // email not registered yet
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Profile profile = user.getProfile();//encode password given
            user.setProfile(profile);
            return userRepository.save(user);
        } else {
            throw new InformationExistException("user with email address " + user.getEmail() + " already exist.");
        }
    }

    public Optional<User> getUser(int id)
    {
        return userRepository.findById(id);
    }

    public void deleteUser(int id)
    {
        userRepository.deleteById(id);
    }

    public User saveUser(User user)
    {
        if(user.getHabits() != null) {
            user.getHabits().forEach(habit -> {
                habit.setUser(user);
                if (habit.getHabitCheckIns() != null) {
                    habit.getHabitCheckIns().forEach(checkIn -> checkIn.setHabit(habit));
                }
            });
        }
        return userRepository.save(user);
    }


    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public User findByEmail(String emailAddress){
        return userRepository.findByEmail(emailAddress);
    }

    public Optional<String> loginUser(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(loginRequest.getEmailAddress(), loginRequest.getPassword());
        try{
            Authentication authentication = authenticationManager.authenticate((authenticationToken)); //authenticate the user
            SecurityContextHolder.getContext().setAuthentication(authentication); //set security context
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal(); //get user details from authenticated object
            return Optional.of(jwtTokenUtil.generateToken(myUserDetails.getUsername())); // generate a token for the authenticated user
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public User updateUserProfile(User user){
        Optional<User> userOptional = Optional.ofNullable(findByEmail(user.getEmail()));
        if(userOptional.isPresent()){ //user exists in database
            //throws error if provided profile is equal to original
            if (userOptional.get().getProfile() == user.getProfile()){
                throw new InformationExistException("Profile details are the same. No update needed.");
            }
            //updates first name if not null and different from original
            if(user.getProfile().getFirstName() != null &&
                    !String.valueOf(userOptional.get().getProfile().getFirstName()).equals(user.getProfile().getFirstName())){
                userOptional.get().getProfile().setFirstName(user.getProfile().getFirstName());
            }
            //updates bio if not null and different from original
            if (user.getProfile().getLastName() != null &&
                    !String.valueOf(userOptional.get().getProfile().getLastName()).equals(user.getProfile().getLastName())){
                userOptional.get().getProfile().setLastName(user.getProfile().getLastName());
            }
            //updates first name if not null and different from original
            if (user.getProfile().getBio() != null &&
                    !String.valueOf(userOptional.get().getProfile().getBio()).equals(user.getProfile().getBio())){
                userOptional.get().getProfile().setBio(user.getProfile().getBio());
            }
            return userRepository.save(userOptional.get());
        } else {
            throw new InformationNotFoundException("user with email address " + user.getEmail() + " not found.");
        }
    }
}
