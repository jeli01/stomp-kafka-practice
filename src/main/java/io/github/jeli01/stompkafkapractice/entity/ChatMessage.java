package io.github.jeli01.stompkafkapractice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;
    private String roomId;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    // 메시지 타입 정의
    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 채팅방 입장
        LEAVE    // 채팅방 퇴장
    }
}
