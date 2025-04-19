package com.multimodule.webshop.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.multimodule.webshop.model.Consumer}
 */
public record ConsumerDto(String username,
                          String firstName,
                          String lastName)
        implements Serializable {
}