package com.mainbrain.services;


import com.mainbrain.models.User;
import com.mainbrain.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public Optional<User> findByUsername(String username){return usersRepository.findByUsername(username);}
    public Optional<User> findByEmail(String email){
        return usersRepository.findByEmail(email);
    }
}
