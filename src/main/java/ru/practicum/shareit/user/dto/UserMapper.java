package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User fromCreateRequest(UserCreateRequest createRequest) {
        User user = new User();
        user.setName(createRequest.getName());
        user.setEmail(createRequest.getEmail());
        return user;
    }

    public static void updateUserFromRequest(UserUpdateRequest updateRequest, User user) {
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
    }
}
