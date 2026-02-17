package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserCreateRequest createRequest);

    UserResponseDto updateUser(Long userId, UserUpdateRequest updateRequest);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    void deleteUser(Long id);
}
