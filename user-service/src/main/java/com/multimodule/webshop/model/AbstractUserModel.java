package com.multimodule.webshop.model;

import com.multimodule.webshop.role.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractUserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_seq",
            sequenceName = "user_seq", allocationSize = 1)
    protected Long id;
    @Column(unique = true, nullable = false)
    protected String username;
    @Column(nullable = false)
    protected String password;
    protected String firstName;
    protected String lastName;
    @CreatedDate
    protected LocalDateTime createdAt;
    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    protected Set<Roles> roles;
    @Column(nullable = false)
    protected boolean isAccountNonExpired;
    @Column(nullable = false)
    protected boolean isAccountNonLocked;
    @Column(nullable = false)
    protected boolean isCredentialsNonExpired;
    @Column(nullable = false)
    protected boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbstractUserModel that = (AbstractUserModel) o;
        return Objects.equals(id, that.id);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }




}



