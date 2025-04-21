package com.multimodule.webshop.mapper;

import com.multimodule.webshop.model.AbstractUserModel;
import com.multimodule.webshop.model.Consumer;
import com.multimodule.webshop.proto.common.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface GrpcUserMapper extends BaseMapper <Consumer, User> {
}
