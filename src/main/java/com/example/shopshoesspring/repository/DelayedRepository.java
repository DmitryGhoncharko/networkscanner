package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.Delayed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayedRepository extends JpaRepository<Delayed, Long> {
}
