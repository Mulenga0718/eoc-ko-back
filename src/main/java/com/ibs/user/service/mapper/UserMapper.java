package com.ibs.user.service.mapper;

import com.ibs.user.domain.User;
import com.ibs.user.dto.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "provider", constant = "LOCAL")
    @Mapping(target = "providerId", ignore = true)
    User toUser(SignUpRequest signUpRequest);
}

