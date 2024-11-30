package ru.yandex.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.mapper.UserMapper;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.ewm.util.LogColorizeUtil.colorizeClass;
import static ru.yandex.practicum.ewm.util.LogColorizeUtil.colorizeMethod;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("{}: Starting execution of {} method.", colorizeClass("UserService"), colorizeMethod("create()"));
        log.info("{}.{}: Mapping from UserDto to User.", colorizeClass("UserService"), colorizeMethod("create()"));

        User user = userMapper.toUser(userDto);

        log.info("{}.{}: Saving user to database.", colorizeClass("UserService"), colorizeMethod("create()"));
        user = userRepository.save(user);
        userDto.setId(user.getId());

        log.info("{}.{}: User saved successfully with id={}.", colorizeClass("UserService"), colorizeMethod("create()"), user.getId());
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("{}: Starting execution of {} method.", colorizeClass("UserService"), colorizeMethod("findAll()"));

        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> usersPage;

        if (ids == null || ids.isEmpty()) {
            log.info("{}.{}: Fetching all users with pagination.", colorizeClass("UserService"), colorizeMethod("findAll()"));
            usersPage = userRepository.findAll(pageable);
        } else {
            log.info("{}.{}: Fetching users by ids with pagination.", colorizeClass("UserService"), colorizeMethod("findAll()"));
            usersPage = userRepository.findByIdIn(ids, pageable);
        }

        List<UserDto> userDtos = usersPage.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("{}.{}: Retrieved {} users.", colorizeClass("UserService"), colorizeMethod("findAll()"), userDtos.size());
        return userDtos;
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.info("{}: Starting execution of {} method.", colorizeClass("UserService"), colorizeMethod("deleteById()"));

        log.info("{}.{}: Checking if user exists with id={}.", colorizeClass("UserService"), colorizeMethod("deleteById()"), userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found.", userId));
        }

        log.info("{}.{}: Deleting user with id={}.", colorizeClass("UserService"), colorizeMethod("deleteById()"), userId);
        userRepository.deleteById(userId);

        log.info("{}.{}: User with id={} deleted successfully.", colorizeClass("UserService"), colorizeMethod("deleteById()"), userId);
    }
}