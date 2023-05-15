package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.ChatMessageRequestDTO;
import org.example.sharing.rest.dto.response.ChatMessageResponseDTO;
import org.example.sharing.store.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    ChatMessageResponseDTO entityToResponse(ChatMessage chatMessage);

    @Mapping(source = "chatRoomId", target = "chatRoom.id")
    ChatMessage requestToEntity(ChatMessageRequestDTO chatMessageRequestDTO);

}
