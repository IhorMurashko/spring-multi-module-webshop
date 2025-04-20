package com.multimodule.webshop.controllers;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.mapper.ConsumerMapper;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.consumerServices.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/consumer")
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;
    private final ConsumerMapper consumerMapper;

    @GetMapping("/get/id/{id}")
    public ResponseEntity<ConsumerDto> getById(@PathVariable Long id) {
        ConsumerDto byId = consumerService.findById(id);
        return ResponseEntity.ok(byId);
    }

    @GetMapping("/get/username/{username}")
    public ResponseEntity<ConsumerDto> getByUsername(@PathVariable String username) {
        ConsumerDto byUsername = consumerService.findByUsername(username);
        return ResponseEntity.ok(byUsername);
    }

    @PatchMapping("/save")
    public ResponseEntity<ConsumerDto> save(@RequestBody ConsumerDto consumerDto) {
        Consumer consumer = consumerMapper.toEntity(consumerDto);
        ConsumerDto save = consumerService.save(consumer);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        consumerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String username) {
        consumerService.deleteByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
