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
public class ChatRoomDto {
    private String roomId;
    private String name;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환
    public static ChatRoomDto fromEntity(ChatRoom entity) {
        return ChatRoomDto.builder()
                .roomId(entity.getRoomId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // DTO -> Entity 변환
    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomId(this.roomId)
                .name(this.name)
                .createdAt(this.createdAt)
                .build();
    }
}
