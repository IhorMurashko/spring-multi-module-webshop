package com.multimodule.webshop.repositories;

import com.multimodule.webshop.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerJpaRepository extends JpaRepository<Consumer, Long> {

    Consumer save(@NonNull Consumer consumer);

    Optional<Consumer> findByUsername(@NonNull String username);

    Optional<Consumer> findById(@NonNull Long id);

    void deleteById(@NonNull Long id);

    void deleteByUsername(@NonNull String username);

    boolean existsById(@NonNull Long id);

    boolean existsByUsername(@NonNull String username);

    @Modifying
    @Query("update Consumer  c set c.password= :newPassword where c.username= :username")
    int updateUserPassword(@NonNull @Param("username") String username,
                               @NonNull @Param("newPassword") String newPassword);

}
