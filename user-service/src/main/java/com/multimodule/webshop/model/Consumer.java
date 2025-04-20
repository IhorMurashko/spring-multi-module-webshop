package com.multimodule.webshop.model;

import com.multimodule.webshop.role.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "consumers")
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Consumer extends AbstractUserModel {

    public Consumer(String email, String password) {
        super.setUsername(email);
        super.setPassword(password);
        super.setAccountNonExpired(true);
        super.setAccountNonLocked(true);
        super.setCredentialsNonExpired(true);
        super.setEnabled(true);
        super.setAuthorities(Set.of(Roles.ROLE_USER));
    }
}
