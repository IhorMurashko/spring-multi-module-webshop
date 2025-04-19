package com.multimodule.webshop.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    }
}
