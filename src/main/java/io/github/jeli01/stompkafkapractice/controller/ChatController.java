package io.github.jeli01.stompkafkapractice.controller;

import io.github.jeli01.stompkafkapractice.entity.ChatMessageDto;
import io.github.jeli01.stompkafkapractice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // /app/chat.sendMessage로 메시지가 전송되면 처리
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto) {
        log.info("Received message from WebSocket: {}", chatMessageDto);
        chatService.sendMessage(chatMessageDto);
    }

    // /app/chat.addUser로 요청이 오면 처리
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDto chatMessageDto) {
        log.info("User joined: {}", chatMessageDto.getSender());
        chatService.sendMessage(chatMessageDto);
    }
}
