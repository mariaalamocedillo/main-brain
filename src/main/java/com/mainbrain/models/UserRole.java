package com.mainbrain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRole implements GrantedAuthority {

    private Role role;


    @Override
    public String getAuthority() {
        return role.getName();
    }
}
