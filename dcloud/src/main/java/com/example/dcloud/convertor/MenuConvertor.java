package com.example.dcloud.convertor;

import com.example.dcloud.dto.MenuDto;
import com.example.dcloud.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MenuConvertor {
    MenuConvertor INSTANCE = Mappers.getMapper(MenuConvertor.class);
    @Mappings({
    })
    MenuDto menuToMenuDto(Menu menu);
}