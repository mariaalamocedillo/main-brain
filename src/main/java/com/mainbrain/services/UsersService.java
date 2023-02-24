package com.mainbrain.services;


import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.repositories.RoleRepository;
import com.mainbrain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

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

    public User findByUsername(String username){return usersRepository.findUserByUsername(username);}
    public User findByEmail(String email){return usersRepository.findUserByEmail(email);}

    public User createUser(String username, String email, String psswd){
        if(checkIfUserExists(email, username) != null){
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(psswd));
        user.setEmail(email);

        UserRole userRole = new UserRole();
        userRole.setRole(roleRepository.findRoleByName("ROLE_USER"));
        user.setUserRoles(new HashSet<>(Collections.singletonList(userRole)));
        usersRepository.insert(user);
        mongoTemplate.save(user);

        return user;
    }

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
