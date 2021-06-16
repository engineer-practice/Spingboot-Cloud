package com.example.dcloud.convertor;

import com.example.dcloud.dto.CreateUserInput;
import com.example.dcloud.entity.Role;
import com.example.dcloud.entity.User;
import com.example.dcloud.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserInputConvertor {
    UserInputConvertor INSTANCE = Mappers.getMapper(UserInputConvertor.class);
    @Mappings({
            @Mapping(source = "user.email",target = "email"),
            @Mapping(source = "user.name",target = "name"),
            @Mapping(source = "user.sno",target = "sno"),
            @Mapping(source = "userRole.roleId",target = "roleId")
    })
    CreateUserInput domain2dto(User user, UserRole userRole);
}