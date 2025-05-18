package io.github.jeli01.stompkafkapractice.service;

import io.github.jeli01.stompkafkapractice.entity.ChatMessage;
import io.github.jeli01.stompkafkapractice.entity.ChatMessageDto;
import io.github.jeli01.stompkafkapractice.entity.ChatRoom;
import io.github.jeli01.stompkafkapractice.entity.ChatRoomDto;
import io.github.jeli01.stompkafkapractice.repository.ChatMessageRepository;
import io.github.jeli01.stompkafkapractice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // 메시지 전송 - Kafka 프로듀서
    public void sendMessage(ChatMessageDto messageDto) {
        // 메시지에 타임스탬프 추가
        if (messageDto.getTimestamp() == null) {
            messageDto.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending message to Kafka: {}", messageDto);
        
        // Kafka 토픽으로 메시지 전송
        kafkaTemplate.send("chat-messages", messageDto);
    }

    // Kafka 컨슈머 - 메시지를 수신하고 웹소켓으로 전달 및 DB에 저장
    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void receiveMessage(ChatMessageDto messageDto) {
        log.info("Received message from Kafka: {}", messageDto);
        
        // 메시지 DB 저장
        ChatMessage savedMessage = chatMessageRepository.save(messageDto.toEntity());
        
        // 웹소켓을 통해 구독자에게 메시지 전달
        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getRoomId(), messageDto);
    }

    // 채팅방 생성
    public ChatRoomDto createChatRoom(String name) {
        String roomId = UUID.randomUUID().toString();
        
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
        
        chatRoomRepository.save(chatRoom);
        
        return ChatRoomDto.fromEntity(chatRoom);
    }

    // 모든 채팅방 조회
    public List<ChatRoomDto> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 채팅방의 메시지 이력 조회
    public List<ChatMessageDto> getChatHistory(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId).stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
