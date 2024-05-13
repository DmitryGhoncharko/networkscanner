package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    void deleteByPath(String path);
}
