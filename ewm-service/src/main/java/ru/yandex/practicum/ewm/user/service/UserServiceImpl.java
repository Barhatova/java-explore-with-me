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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("Запрос на создание пользователя");
        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        userDto.setId(user.getId());
        log.info("Пользователь создан");
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("Запрос на получение списка пользователей");
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> usersPage;
        if (ids == null || ids.isEmpty()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            usersPage = userRepository.findByIdIn(ids, pageable);
        }
        List<UserDto> userDtos = usersPage.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Список пользователей получен");
        return userDtos;
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.info("Запрос на удаление пользователя по идентификатору");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь не найден", userId));
        }
        userRepository.deleteById(userId);
        log.info("Пользователь по идентификатору удален");
    }
}