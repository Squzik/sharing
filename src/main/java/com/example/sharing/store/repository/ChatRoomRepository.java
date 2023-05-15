package org.example.sharing.store.repository;

import org.example.sharing.store.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(value = "select * from chat_room c where c.first_user = ?1 or c.second_user = ?1", nativeQuery = true)
    List<ChatRoom> findByFirstUserOrSecondUser(UUID id);
}
