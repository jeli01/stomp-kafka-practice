package io.github.jeli01.stompkafkapractice.repository;

import io.github.jeli01.stompkafkapractice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);
}
