package com.mainbrain.services;

import com.mainbrain.controllers.NotesController;
import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotesController.class);

    private final AuthenticationManager AUTHENTICATIONMANAGER;

    private final UserDetailsService USERDETAILSSERVICE;

    private final UserRepository userRepository; // inyecta el repositorio de usuarios

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityServiceImpl(AuthenticationManager authenticationManager,
                               UserDetailsService userDetailsService, UserRepository userRepository) {
        this.AUTHENTICATIONMANAGER = authenticationManager;
        this.USERDETAILSSERVICE = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            return false;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);
            LOGGER.info("User has been authenticated");
            return true;
        }
        LOGGER.info("User hasn't been authenticated");
        return false;
    }


    public boolean register(User _user) {
        if (userRepository.findByEmail(_user.getEmail()) != null
                || userRepository.findByUsername(_user.getUsername()) != null ) {
            // El usuario ya existe
            return false;
        }
        // Crear una nueva instancia de UserDetails
        User user = User.builder()
                .username(_user.getUsername())
                .email(_user.getEmail())
                .password(bCryptPasswordEncoder.encode(_user.getPassword()))
                .userRoles(Collections.singleton(new UserRole(new Role("ROLE_USER"))))
                .notesIds(new ArrayList<>())
                .build();

        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

        userRepository.save(user);

        // Iniciar sesión automáticamente después de registrarse
        return this.login(user.getUsername(), user.getPassword());
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}
