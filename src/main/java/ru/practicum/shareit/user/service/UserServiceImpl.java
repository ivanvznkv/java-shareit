package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage userStorage;

    @Override
    public UserResponseDto createUser(UserCreateRequest createRequest) {
        User user = UserMapper.fromCreateRequest(createRequest);
        User createdUser = userStorage.create(user);
        return UserMapper.toResponseDto(createdUser);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateRequest updateRequest) {
        User existingUser = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        UserMapper.updateUserFromRequest(updateRequest, existingUser);

        User updatedUser = userStorage.update(existingUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userStorage.findById(id).isPresent()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userStorage.delete(id);
    }
}
