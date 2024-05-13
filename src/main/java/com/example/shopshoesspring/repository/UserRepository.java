package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
        Optional<User> findUserByUserLogin(String login);
}
