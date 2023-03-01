package com.mainbrain.services;


import com.mainbrain.models.User;
import com.mainbrain.repositories.RoleRepository;
import com.mainbrain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<User> findById(String id){return usersRepository.findById(id);}
    public User findByUsername(String username){return usersRepository.findByUsername(username);}
    public User findByEmail(String email){return usersRepository.findByEmail(email);}


    public void deleteId(String username, String noteId) {
        User user = findByUsername(username);
        user.getNotesIds().removeIf(note -> note.getId().toString().equals(noteId));
        usersRepository.save(user);
    }

    public String checkIfUserExists(String email, String username){
        //get the user if it already exists TODO comprobar email
        if (findByUsername(username) != null){
            return "username";
        } else if (findByEmail(email) != null){
            return "email";
        }
        return null;
    }
}
