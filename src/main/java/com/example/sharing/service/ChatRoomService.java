package org.example.sharing.service;

import org.example.sharing.rest.dto.request.ChatMessageRequestDTO;
import org.example.sharing.rest.dto.request.ChatRoomRequestDTO;
import org.example.sharing.rest.dto.response.ChatMessageResponseDTO;
import org.example.sharing.rest.dto.response.ChatRoomResponseDTO;
import org.example.sharing.rest.dto.response.MessageListResponseDto;

import java.util.List;
import java.util.UUID;

public interface ChatRoomService {

    ChatRoomResponseDTO createRoom(ChatRoomRequestDTO chatRoomRequestDTO);

    List<ChatRoomResponseDTO> getRooms();

    ChatMessageResponseDTO notifyFrontend(ChatMessageRequestDTO chatMessageRequestDTO);

    MessageListResponseDto getMessages(UUID chatRoomId);
}
