package com.example.shopshoesspring.auth.config.auth;

import com.example.shopshoesspring.entity.BannedUser;
import com.example.shopshoesspring.entity.User;
import com.example.shopshoesspring.repository.BannedUserRepository;
import com.example.shopshoesspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BannedUserRepository bannedUserRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userLogin) throws UsernameNotFoundException {
        Optional<BannedUser> bannedUserOptional = bannedUserRepository.findByUser_UserLogin(userLogin);
        if (bannedUserOptional.isPresent()) {
            throw new UsernameNotFoundException(userLogin);
        }
        Optional<User> userOptional = userRepository.findUserByUserLogin(userLogin);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return org.springframework.security.core.userdetails.User.builder().
                    username(user.getUserLogin()).
                    password(user.getUserPassword()).
                    roles(user.getUserRole().getRoleName()).
                    build();
        } else {
            throw new UsernameNotFoundException(userLogin);
        }

    }
}
