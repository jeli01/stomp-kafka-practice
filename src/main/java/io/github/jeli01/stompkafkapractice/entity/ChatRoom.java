package io.github.jeli01.stompkafkapractice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roomId;  // 클라이언트에서 사용할 고유 ID (UUID 등으로 생성)

    private String name;
    private LocalDateTime createdAt;
}
