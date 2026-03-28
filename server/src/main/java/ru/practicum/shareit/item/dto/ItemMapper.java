package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    public static ItemResponseDto toResponseDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return dto;
    }

    public static Item fromCreateRequest(ItemCreateRequest request, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return item;
    }

    public static void updateItemFromRequest(ItemUpdateRequest request, Item item) {
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }
    }

    public static ItemDetailedDto toDetailedDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentResponseDto> comments) {
        ItemDetailedDto dto = new ItemDetailedDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }

    public static ItemWithBookingsDto toWithBookingsDto(Item item,
                                                        BookingShortDto lastBooking,
                                                        BookingShortDto nextBooking,
                                                        List<CommentResponseDto> comments) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }
}
