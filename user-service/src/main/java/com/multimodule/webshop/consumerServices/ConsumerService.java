package com.multimodule.webshop.consumerServices;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.model.Consumer;

public interface ConsumerService {

    ConsumerDto save(Consumer consumer);

    ConsumerDto findByUsername(String username);

    ConsumerDto findById(Long id);

    boolean existsByUsername(String username);

    void deleteById(Long id);

    void deleteByUsername(String username);

}
