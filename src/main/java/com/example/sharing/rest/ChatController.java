package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.ChatMessageRequestDTO;
import org.example.sharing.rest.dto.request.ChatRoomRequestDTO;
import org.example.sharing.rest.dto.response.ChatMessageResponseDTO;
import org.example.sharing.rest.dto.response.ChatRoomResponseDTO;
import org.example.sharing.rest.dto.response.MessageListResponseDto;
import org.example.sharing.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@SecurityRequirement(name = SCHEME_NAME)
@Tag(name = "ChatController", description = "Контроллер чата")
public class ChatController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/createRoom")
    @Operation(summary = "создание румы")
    public ResponseEntity<ChatRoomResponseDTO> createRoom(
            @Valid @RequestBody ChatRoomRequestDTO chatRoomRequestDTO
    ) {
        ChatRoomResponseDTO chatRoomResponseDTO = chatRoomService.createRoom(chatRoomRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(chatRoomResponseDTO.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(chatRoomResponseDTO);
    }

    @GetMapping("/rooms")
    @Operation(summary = "получение всех рум по пользователю")
    public List<ChatRoomResponseDTO> getRooms() {
        return chatRoomService.getRooms();
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ChatMessageResponseDTO getMessage(
            @Valid @RequestBody ChatMessageRequestDTO chatMessageRequestDTO
    ) {
        return chatRoomService.notifyFrontend(chatMessageRequestDTO);
    }

    @PostMapping("/sendMes")
    public ChatMessageResponseDTO sendMessage(@RequestBody ChatMessageRequestDTO chatMessageRequestDTO) {
        return chatRoomService.notifyFrontend(chatMessageRequestDTO);
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<MessageListResponseDto> getMessages(@Parameter(in = ParameterIn.PATH,
            name = "chatRoomId",
            description = "ID румы",
            schema = @Schema(type = "uuid"))
                                                              @PathVariable UUID chatRoomId) {
        return ResponseEntity.ok(chatRoomService.getMessages(chatRoomId));
    }
}