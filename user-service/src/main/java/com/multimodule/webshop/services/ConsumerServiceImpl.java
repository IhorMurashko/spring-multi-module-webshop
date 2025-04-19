package com.multimodule.webshop.services;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.exceptions.ConsumerIdException;
import com.multimodule.webshop.exceptions.ConsumerUsernameNotFoundException;
import com.multimodule.webshop.mapper.ConsumerMapper;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.repositories.ConsumerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final ConsumerJpaRepository consumerJpaRepository;
    private final ConsumerMapper consumerMapper;

    @Transactional(readOnly = false)
    @Override
    public ConsumerDto save(@NonNull ConsumerDto consumerDto) {

        Consumer entity = consumerMapper.toEntity(consumerDto);
        Consumer saved = consumerJpaRepository.save(entity);
        log.info("Saved consumer with id: {} and username: {}",
                saved.getId(), saved.getUsername());

        return consumerMapper.toDto(saved);
    }


    @Override
    public ConsumerDto findByUsername(@NonNull String username) {

        Consumer consumer = consumerJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ConsumerUsernameNotFoundException("Username " + username + " not found"));

        log.info("Found consumer with username: {}",
                consumer.getUsername());

        return consumerMapper.toDto(consumer);
    }

    @Override
    public ConsumerDto findById(@NonNull Long id) {

        Consumer consumer = consumerJpaRepository.findById(id)
                .orElseThrow(() -> new ConsumerIdException("Id " + id + " not found"));

        log.info("Found consumer with ID: {}",
                consumer.getId());

        return consumerMapper.toDto(consumer);
    }

    @Transactional
    @Override
    public void deleteById(@NonNull Long id) {

        boolean existsById = consumerJpaRepository.existsById(id);
        if (existsById) {
            consumerJpaRepository.deleteById(id);

            log.warn("Deleted consumer with id: {}", id);
        } else {
            throw new ConsumerIdException("Id " + id + " not found");
        }

    }

    @Transactional
    @Override
    public void deleteByUsername(@NonNull String username) {

        boolean existsByUsername = consumerJpaRepository.existsByUsername(username);
        if (existsByUsername) {
            consumerJpaRepository.deleteByUsername(username);
            log.warn("Deleted consumer with username: {}", username);
        } else {
            throw new ConsumerUsernameNotFoundException("Username " + username + " not found");
        }
    }
}
