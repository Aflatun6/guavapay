package com.parcel.app.security;

import com.parcel.app.entity.UserEntity;
import com.parcel.app.repo.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> byUsername = userRepository
                .findByUsername(username);

        UserDetailsImp userDetailsImp = byUsername.map(UserDetailsImp::new)
                .orElseThrow(() -> new UsernameNotFoundException("user with name " + username + " cant be found :(("));

        return userDetailsImp;

    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user with name " + username + " cant be found :(("));
    }

    public UserEntity findById(String userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("user with id " + userId + " cant be found :("));
    }
}
