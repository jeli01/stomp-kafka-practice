package io.github.jeli01.stompkafkapractice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String sender;
    private String content;
    private String roomId;
    private LocalDateTime timestamp;
    private ChatMessage.MessageType type;

    // Entity -> DTO 변환
    public static ChatMessageDto fromEntity(ChatMessage entity) {
        return ChatMessageDto.builder()
                .sender(entity.getSender())
                .content(entity.getContent())
                .roomId(entity.getRoomId())
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .build();
    }

    // DTO -> Entity 변환
    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .sender(this.sender)
                .content(this.content)
                .roomId(this.roomId)
                .timestamp(this.timestamp)
                .type(this.type)
                .build();
    }
}
