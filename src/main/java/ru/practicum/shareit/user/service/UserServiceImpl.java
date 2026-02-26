package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserCreateRequest createRequest) {
        if (userRepository.existsByEmailAndIdNot(createRequest.getEmail(), -1L)) {
            throw new DuplicateEmailException("Email уже используется");
        }
        User user = UserMapper.fromCreateRequest(createRequest);
        User saved = userRepository.save(user);
        return UserMapper.toResponseDto(saved);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateRequest updateRequest) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (updateRequest.getEmail() != null &&
                !updateRequest.getEmail().equals(existing.getEmail()) &&
                userRepository.existsByEmailAndIdNot(updateRequest.getEmail(), userId)) {
            throw new DuplicateEmailException("Email уже используется");
        }

        UserMapper.updateUserFromRequest(updateRequest, existing);
        User updated = userRepository.save(existing);
        return UserMapper.toResponseDto(updated);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}
