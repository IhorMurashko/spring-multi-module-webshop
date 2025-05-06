package com.multimodule.webshop.mapper;

import com.multimodule.security.userDetails.CustomUserDetails;
import com.multimodule.webshop.proto.common.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserToCustomUserDetailsDtoMapper extends BaseMapper<User, CustomUserDetails> {
}
