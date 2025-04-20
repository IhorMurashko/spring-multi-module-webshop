package com.multimodule.webshop.consumerServices;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.exceptions.ConsumerIdException;
import com.multimodule.webshop.exceptions.ConsumerUsernameNotFoundException;
import com.multimodule.webshop.mapper.ConsumerMapper;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.repositories.ConsumerJpaRepository;
import jakarta.validation.constraints.NotNull;
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
    public ConsumerDto save(@NonNull Consumer consumer) {

        Consumer saved = consumerJpaRepository.save(consumer);
        log.info("Saved consumer with id: {} and username: {}",
                saved.getId(), saved.getUsername());

        return consumerMapper.toDto(saved);
    }


    @Override
    public ConsumerDto findByUsername(@NonNull String username) {

        Consumer consumer = consumerJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ConsumerUsernameNotFoundException(String.format("Consumer with username %s not found", username)));

        log.info("Found consumer with username: {}",
                consumer.getUsername());

        return consumerMapper.toDto(consumer);
    }

    @Override
    public ConsumerDto findById(@NonNull Long id) {

        Consumer consumer = consumerJpaRepository.findById(id)
                .orElseThrow(() -> new ConsumerIdException(String.format("Consumer with id %d not found", id)));

        log.info("Found consumer with ID: {}",
                consumer.getId());

        return consumerMapper.toDto(consumer);
    }

    @Override
    public boolean existsByUsername(@NotNull String username) {
        return consumerJpaRepository.existsByUsername(username);
    }

    @Transactional
    @Override
    public void deleteById(@NonNull Long id) {

        boolean existsById = consumerJpaRepository.existsById(id);
        if (existsById) {
            consumerJpaRepository.deleteById(id);

            log.warn("Deleted consumer with id: {}", id);
        } else {
            throw new ConsumerIdException(String.format("Consumer with id %d not found", id));
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
            throw new ConsumerUsernameNotFoundException(String.format("Consumer with username %s not found", username));
        }
    }
}
