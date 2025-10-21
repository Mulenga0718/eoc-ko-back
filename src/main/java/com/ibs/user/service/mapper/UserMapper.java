package com.ibs.user.service.mapper;

import com.ibs.user.domain.User;
import com.ibs.user.dto.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "loginId", target = "username") // loginId -> username 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "provider", constant = "LOCAL")
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toUser(SignUpRequest signUpRequest);
}
