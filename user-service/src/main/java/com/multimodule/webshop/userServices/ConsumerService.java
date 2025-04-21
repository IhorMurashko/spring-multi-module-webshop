package com.multimodule.webshop.userServices;

import com.multimodule.webshop.model.Consumer;

public interface ConsumerService {

    Consumer save(com.multimodule.webshop.model.Consumer consumer);

    Consumer findByUsername(String username);

    Consumer findById(Long id);

    boolean existsByUsername(String username);

    void deleteById(Long id);

    void deleteByUsername(String username);

    String resetPassword(String username);
}
