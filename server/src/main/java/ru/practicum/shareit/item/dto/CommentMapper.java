package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentResponseDto toResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public static Comment fromCreateRequest(CommentCreateRequest request, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }
}
