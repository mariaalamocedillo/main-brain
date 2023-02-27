package com.mainbrain.services;

import java.util.HashSet;
import java.util.Set;

import com.mainbrain.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MongoAuthUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public MongoAuthUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        com.mainbrain.models.User user = userRepository.findByUsername(userName);
        if(user == null){
            return null;
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

}
