package com.example.shopshoesspring;
import jcifs.smb.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopshoesspringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopshoesspringApplication.class, args);
    }
}
