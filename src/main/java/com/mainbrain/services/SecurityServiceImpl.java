package com.mainbrain.services;

import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.repositories.UserRepository;
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

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository; // inyecta el repositorio de usuarios

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityServiceImpl(AuthenticationManager authenticationManager,
                               UserDetailsService userDetailsService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            return false;
        }

        System.out.println("RESULTADO:"+ user.getPassword() + "----" + bCryptPasswordEncoder.matches(password, user.getPassword()));
        if (bCryptPasswordEncoder.matches(password, user.getPassword())){
            System.out.println("SI FUNCIONA EL LOGEARSEAKRNF");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());

        System.out.println("--- UsernamePasswordAuthenticationToken: ::::::" + usernamePasswordAuthenticationToken);

        System.out.println("<<< authenticationManager.authenticate(): ::::::");
        //authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        System.out.println("<<< AUTENTICADO PASADO ::::::");

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("ESTAMOS EN EL IF");
            return true;
        }
        System.out.println("PASAMOS EL IF SIN ENTRAR");
        return false;
    }


    public boolean register(User _user) {
        if (userRepository.findByEmail(_user.getEmail()) != null
                || userRepository.findByUsername(_user.getUsername()) != null ) {
            // El usuario ya existe
            System.out.println("************************ No se pudo crear porque usuario ya esta registrado");
            return false;
        }

        System.out.println("************************ Registrando usuario...");

        // Crear una nueva instancia de UserDetails
        User user = User.builder()
                .username(_user.getUsername())
                .email(_user.getEmail())
                .password(bCryptPasswordEncoder.encode(_user.getPassword()))
                .userRoles(Collections.singleton(new UserRole(new Role("ROLE_USER"))))
                .notesIds(new ArrayList<>())
                .build();

        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

/*
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
*/

        System.out.println(userRepository.save(user));

        // Iniciar sesión automáticamente después de registrarse
        return this.login(user.getUsername(), user.getPassword());
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}
