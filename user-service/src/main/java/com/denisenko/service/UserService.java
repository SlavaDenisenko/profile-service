package com.denisenko.service;

import com.denisenko.dto.UserDto;
import com.denisenko.entity.User;
import com.denisenko.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);

    @Inject
    UserMapper userMapper;

    public UserDto getUser(Integer id) {
        return userMapper.toDto(User.findById(id));
    }

    @Transactional
    public UserDto updateUser(Integer id, UserDto userDto) {
        Optional<User> optionalUser = User.findByIdOptional(id);
        User user;
        userDto.setId(id);
        if (optionalUser.isEmpty()) {
            user = userMapper.toEntity(userDto);
            user.persist();
            LOG.info("New user was created with username=" + user.getUsername());
        } else {
            user = optionalUser.get();
            LOG.info("Updating user with username=" + user.getUsername());
            userMapper.updateEntityFromDto(userDto, user);
        }
        return userMapper.toDto(user);
    }
}
