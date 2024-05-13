package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.BannedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {
    Optional<BannedUser> findByUser_UserLogin(String userLogin);
}
