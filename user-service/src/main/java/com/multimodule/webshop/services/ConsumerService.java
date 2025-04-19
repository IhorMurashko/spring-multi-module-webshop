package com.multimodule.webshop.services;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.model.Consumer;

import java.util.Optional;

public interface ConsumerService {

    ConsumerDto save(ConsumerDto consumerDto);

    ConsumerDto findByUsername(String username);

    ConsumerDto findById(Long id);

    void deleteById(Long id);

    void deleteByUsername(String username);

}
