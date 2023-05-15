package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.ChatMessageRequestDTO;
import org.example.sharing.rest.dto.request.ChatRoomRequestDTO;
import org.example.sharing.rest.dto.response.ChatMessageResponseDTO;
import org.example.sharing.rest.dto.response.ChatRoomResponseDTO;
import org.example.sharing.rest.dto.response.MessageListResponseDto;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.mapper.ChatMessageMapper;
import org.example.sharing.rest.mapper.ChatRoomMapper;
import org.example.sharing.service.ChatRoomService;
import org.example.sharing.store.entity.ChatMessage;
import org.example.sharing.store.entity.ChatRoom;
import org.example.sharing.store.repository.ChatMessageRepository;
import org.example.sharing.store.repository.ChatRoomRepository;
import org.example.sharing.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.sharing.utils.ServiceUtils.getUserIdFromCurrentSession;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomMapper roomMapper;

    private final ChatMessageMapper chatMapper;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public ChatRoomResponseDTO createRoom(ChatRoomRequestDTO chatRoomRequestDTO) throws BadRequestException {
        if (chatRoomRequestDTO.getFirstUserId().equals(chatRoomRequestDTO.getSecondUserId())) {
            throw new BadRequestException(BadRequestException.CHAT_ROOM_CREATE);
        }
        return roomMapper.chatRoomToResponse(chatRoomRepository.save(
                new ChatRoom(
                        userRepository.getById(chatRoomRequestDTO.getFirstUserId()),
                        userRepository.getById(chatRoomRequestDTO.getSecondUserId())
                )));
    }

    @Override
    public List<ChatRoomResponseDTO> getRooms() {
        List<ChatRoom> chatRoom = chatRoomRepository.findByFirstUserOrSecondUser(getUserIdFromCurrentSession());
        return chatRoom.stream().map(roomMapper::chatRoomToResponse).collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponseDTO notifyFrontend(ChatMessageRequestDTO chatMessageRequestDTO) {
        ChatMessage chatMessage = chatMapper.requestToEntity(chatMessageRequestDTO);
        chatMessage.setDateTime(LocalDateTime.now());
        chatMessage.setId(getUserIdFromCurrentSession());

        simpMessagingTemplate.convertAndSend("/topic/messages", chatMessageRequestDTO);
        return chatMapper.entityToResponse(chatMessageRepository.save(chatMessage));
    }

    @Override
    public MessageListResponseDto getMessages(UUID chatRoomId) {
        MessageListResponseDto messageListResponseDto = MessageListResponseDto.builder()
                .chatRoomId(chatRoomId)
                .build();
        chatMessageRepository.findAllByChatRoomId(chatRoomId).forEach(chatMessage -> messageListResponseDto
                .getMessages()
                .add(chatMapper.entityToResponse(chatMessage)));
        return messageListResponseDto;
    }
}
