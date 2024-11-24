package ru.yandex.practicum.ewm.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.ewm.exception.ObjectNotFoundException;
import ru.yandex.practicum.ewm.user.dto.NewUserRequest;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.mapper.UserMapper;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;

    @Override
    public UserDto createUserAdmin(NewUserRequest newUserRequest) {
        if (userRepository.existsUserByEmail(newUserRequest.getEmail())) {
            throw new ObjectAlreadyExistException(String.format("Пользовать с почтой {} уже существует", newUserRequest.getEmail()));
        }
        return userMapper.toDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsersAdmin(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
        return userRepository.getByIdIn(ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь по id {} не найден", id));
        }
        userRepository.deleteById(id);
    }
}
