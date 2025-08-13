package com.nikita.habittracker.repository;

import com.nikita.habittracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByEmail(String email);

    Optional<User> findByUserName(String name);
}
