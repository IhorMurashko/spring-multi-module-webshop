package com.multimodule.webshop.mapper;

import com.multimodule.webshop.dto.ConsumerDto;
import com.multimodule.webshop.model.Consumer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsumerMapper extends BaseMapper<Consumer, ConsumerDto> {
}
