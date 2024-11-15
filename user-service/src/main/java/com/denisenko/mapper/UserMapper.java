package com.denisenko.mapper;

import com.denisenko.dto.UserDto;
import com.denisenko.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "jakarta")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Mapping(target = "username", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
