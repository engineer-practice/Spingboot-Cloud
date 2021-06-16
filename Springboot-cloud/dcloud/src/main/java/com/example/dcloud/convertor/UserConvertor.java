package com.example.dcloud.convertor;
import com.example.dcloud.dto.UserDto;
import com.example.dcloud.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserConvertor {
    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);
    @Mappings({
            @Mapping(source = "user.name",target = "name"),
            @Mapping(source = "user.nickname",target = "nickname"),
            @Mapping(source = "user.image",target = "image"),
            @Mapping(source = "user.sno",target = "sno"),
            @Mapping(source = "user.password",target = "password")
    })
    UserDto domain2dto(User user);
}