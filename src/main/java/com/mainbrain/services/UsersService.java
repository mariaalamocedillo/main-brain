package com.mainbrain.services;


import com.mainbrain.config.JwtTokenProvider;
import com.mainbrain.models.User;
import com.mainbrain.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersService {

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public Optional<User> findById(String id){return usersRepository.findById(id);}
    public User findByUsername(String username){return usersRepository.findByUsername(username);}
    public User findByEmail(String email){return usersRepository.findByEmail(email);}

    public List<Map<String, String>> findAllUsers(User userAuthor){
        List<User> users = usersRepository.findAll();
        users.remove(userAuthor);

        List<Map<String, String>> usersList = new ArrayList<>();
        for (User user: users) {
            Map<String, String> usersMap = new HashMap<>();
            usersMap.put("value", user.getUsername());
            usersMap.put("label", user.getUsername());
            usersList.add(usersMap);
        }
        return usersList;
    }

    public void deleteId(String username, String noteId) {
        User user = findByUsername(username);
        user.getNotesIds().removeIf(note -> note.getId().toString().equals(noteId));
        usersRepository.save(user);
    }

    //method that checks if the session token is valid
    public User checkAuthenticated(HttpServletRequest request) {
        // check if the token is valid
        String userToken = request.getHeader("Authorization");
        if (userToken == null || userToken.isEmpty()) {
            System.out.println("There is no active session ");
            return null;
        } else if (tokenProvider.isTokenValid(userToken)) {
            // Aquí valida el token y obtiene la información del usuario si existe
            User userDetails = tokenProvider.getUserPrincipalFromToken(tokenProvider.resolveToken(request));
            Optional<User> _user = findById(userDetails.getUsername());
            if (_user.isPresent()){
                System.out.println("There is a session: User was found");
                return _user.get();
            }
            System.out.println("There is a session: User not found");
        }
        System.out.println("The session is invalid");
        return null;
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
