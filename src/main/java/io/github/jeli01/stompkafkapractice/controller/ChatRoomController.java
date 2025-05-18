package io.github.jeli01.stompkafkapractice.controller;

import io.github.jeli01.stompkafkapractice.entity.ChatMessageDto;
import io.github.jeli01.stompkafkapractice.entity.ChatRoomDto;
import io.github.jeli01.stompkafkapractice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatService chatService;

    // 새 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody Map<String, String> payload) {
        String roomName = payload.get("name");
        return ResponseEntity.ok(chatService.createChatRoom(roomName));
    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms() {
        return ResponseEntity.ok(chatService.getAllChatRooms());
    }

    // 특정 채팅방의 메시지 이력 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId));
    }
}
