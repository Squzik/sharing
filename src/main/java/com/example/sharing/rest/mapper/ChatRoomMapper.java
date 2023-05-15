package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.ChatRoomRequestDTO;
import org.example.sharing.rest.dto.response.ChatRoomResponseDTO;
import org.example.sharing.store.entity.ChatRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ChatMessageMapper.class, UserMapper.class})
public interface ChatRoomMapper {

    ChatRoomResponseDTO chatRoomToResponse(ChatRoom chatRoom);

    @Mapping(source = "firstUserId", target = "firstUser.id")
    @Mapping(source = "secondUserId", target = "secondUser.id")
    ChatRoom chatRoomRequestDTOToEntity(ChatRoomRequestDTO chatRoomRequestDTO);
}
